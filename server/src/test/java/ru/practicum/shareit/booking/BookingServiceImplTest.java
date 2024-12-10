package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner = User.builder()
            .id(1L)
            .name("name")
            .email("name@mail.ru")
            .build();

    private User tenant = User.builder()
            .id(2L)
            .name("booker")
            .email("booker@mail.ru")
            .build();

    private Item item = Item.builder()
            .name("item")
            .description("description")
            .available(true)
            .owner(owner)
            .id(1L)
            .build();

    private Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .tenant(tenant)
            .item(item)
            .status(BookingStatus.WAITING)
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .itemId(1L)
            .build();

    @Test
    void testCanAddBooking() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(tenant));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.save(any())).thenReturn(booking);

        ResponseBookingDto response = BookingMapper.mapToResponse(booking);
        ResponseBookingDto response1 = bookingService.addBookingRequest(bookingDto, 2L);

        assertThat(response.getId(), equalTo(response1.getId()));
    }

    @Test
    void testThrowNotFoundIfTryAddBookingWithNotExistsUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBookingRequest(bookingDto, 2L));

        verify(bookingStorage, never()).save(any());
    }

    @Test
    void testThrowNotFoundIfTryAddBookingWithNotExistsItem() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(tenant));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBookingRequest(bookingDto, 2L));

        verify(bookingStorage, never()).save(any());
    }

    @Test
    void testThrowValidationExceptionIfTryBookingNotAvailableItem() {
        item.setAvailable(false);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(tenant));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.addBookingRequest(bookingDto, tenant.getId()));

        verify(bookingStorage, never()).save(any());
    }

    @Test
    void testThrowValidationExceptionIfStartEqualsEnd() {
        bookingDto.setEnd(bookingDto.getStart());
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(tenant));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.addBookingRequest(bookingDto, tenant.getId()));

        verify(bookingStorage, never()).save(any());
    }

    @Test
    void testOwnerCanApproveBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        ResponseBookingDto response = BookingMapper.mapToResponse(booking);
        ResponseBookingDto response1 = bookingService.approveBooking(booking.getId(), 1L, true);

        assertThat(response.getId(), equalTo(response1.getId()));
        assertThat(BookingStatus.APPROVED, equalTo(response1.getStatus()));
    }

    @Test
    void testOwnerCanRejectBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        ResponseBookingDto response = BookingMapper.mapToResponse(booking);
        ResponseBookingDto response1 = bookingService.approveBooking(booking.getId(), 1L, false);

        assertThat(response.getId(), equalTo(response1.getId()));
        assertThat(BookingStatus.REJECTED, equalTo(response1.getStatus()));
    }

    @Test
    void testThrowValidationExceptionIfStatusNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(booking.getId(), 1L, false));

        verify(bookingStorage, never()).save(booking);
    }

    @Test
    void testThrowAuthorizationExceptionIfNotOwnerTryApproveBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AuthorizationException.class,
                () -> bookingService.approveBooking(booking.getId(), 2L, false));

        verify(bookingStorage, never()).save(booking);
    }

    @Test
    void testOwnerCanGetBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseBookingDto response = BookingMapper.mapToResponse(booking);
        ResponseBookingDto response1 = bookingService.getBooking(booking.getId(), 1L);

        assertThat(response.getId(), equalTo(response1.getId()));
        assertThat(response.getStart(), equalTo(response1.getStart()));
        assertThat(response.getEnd(), equalTo(response1.getEnd()));
    }

    @Test
    void testTenantCanGetBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseBookingDto response = BookingMapper.mapToResponse(booking);
        ResponseBookingDto response1 = bookingService.getBooking(booking.getId(), 2L);

        assertThat(response.getId(), equalTo(response1.getId()));
        assertThat(response.getStart(), equalTo(response1.getStart()));
        assertThat(response.getEnd(), equalTo(response1.getEnd()));
    }

    @Test
    void testThrowAuthorizationExceptionIfNotOwnerOrNotTenantTryGetBooking() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AuthorizationException.class, () -> bookingService.getBooking(booking.getId(), 3L));
    }

    @Test
    void testCanGetAllBookingsOfTenant() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings = List.of(booking);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(tenant));
        when(bookingStorage.findAllByTenantId(tenant.getId(), sort)).thenReturn(bookings);

        assertThat(bookings.stream().map(BookingMapper::mapToResponse).toList().getFirst().getId(),
                equalTo(bookingService.getBookings(tenant.getId(), BookingState.ALL).getFirst().getId()));

        verify(bookingStorage, times(1)).findAllByTenantId(tenant.getId(), sort);
    }
}