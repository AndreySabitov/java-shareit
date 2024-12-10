package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "jdbc.url=jdbc:postgresql://localhost:6541/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private User owner;
    private Item item;
    private User tenant;

    @BeforeEach
    void init() {
        owner = userStorage.save(User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build());
        item = itemStorage.save(Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build());
        tenant = userStorage.save(User.builder()
                .name("tenantName")
                .email("tenant@mail.ru")
                .build());
    }

    @Test
    void testCanGetPastBookingsOfTenant() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookings(tenant.getId(), BookingState.PAST);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetFutureBookingsOfTenant() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookings(tenant.getId(), BookingState.FUTURE);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetCurrentBookingsOfTenant() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookings(tenant.getId(), BookingState.CURRENT);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetRejectedBookingsOfTenant() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookings(tenant.getId(), BookingState.REJECTED);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetAllBookingsOfAllItemsOfOwner() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookingsOfAllItemsOfOwner(owner.getId(),
                BookingState.ALL);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetPastBookingsOfAllItemsOfOwner() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookingsOfAllItemsOfOwner(owner.getId(),
                BookingState.PAST);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetFutureBookingsOfAllItemsOfOwner() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookingsOfAllItemsOfOwner(owner.getId(),
                BookingState.FUTURE);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetCurrentBookingsOfAllItemsOfOwner() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookingsOfAllItemsOfOwner(owner.getId(),
                BookingState.CURRENT);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }

    @Test
    void testCanGetWaitingBookingsOfAllItemsOfOwner() {
        Booking booking = bookingStorage.save(Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .tenant(tenant)
                .item(item)
                .build());

        List<ResponseBookingDto> bookings = bookingService.getBookingsOfAllItemsOfOwner(owner.getId(),
                BookingState.WAITING);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking.getId(), equalTo(bookings.getFirst().getId()));
    }
}