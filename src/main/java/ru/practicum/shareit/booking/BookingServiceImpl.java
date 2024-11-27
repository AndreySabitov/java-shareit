package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public ResponseBookingDto addBookingRequest(BookingDto bookingDto, Integer userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для бронирования");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала и конца бронирования не могут совпадать");
        }
        return BookingMapper.mapToResponse(
                bookingStorage.save(BookingMapper.mapToBooking(bookingDto, user, item, BookingStatus.WAITING)));
    }

    @Override
    @Transactional
    public ResponseBookingDto approveBooking(BigInteger bookingId, Integer userId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new AuthorizationException("Подтвердить бронирование может только хозяин вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            booking.getItem().setAvailable(false);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapToResponse(bookingStorage.save(booking));
    }

    @Override
    public ResponseBookingDto getBooking(BigInteger bookingId, Integer userId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getTenant().getId(), userId)) {
            throw new AuthorizationException("Просмотреть бронирование может только арендатор или хозяин вещи");
        }
        return BookingMapper.mapToResponse(booking);
    }

    @Override
    public List<ResponseBookingDto> getBookings(Integer userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ResponseBookingDto> bookings = bookingStorage.findAllByTenantId(userId).stream()
                .map(BookingMapper::mapToResponse)
                .sorted(Comparator.comparing(ResponseBookingDto::getStart).reversed())
                .toList();
        if (state == BookingState.ALL) {
            return bookings;
        } else {
            return bookings.stream()
                    .filter(bookingDto -> bookingDto.getState() == state)
                    .toList();
        }
    }

    @Override
    public List<ResponseBookingDto> getBookingsOfAllItemsOfOwner(Integer userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ResponseBookingDto> bookings = bookingStorage.findAllBookingsOfItemsOfOwner(userId).stream()
                .map(BookingMapper::mapToResponse)
                .toList();
        if (state == BookingState.ALL) {
            return bookings;
        } else {
            return bookings.stream()
                    .filter(bookingDto -> bookingDto.getState() == state)
                    .toList();
        }
    }
}