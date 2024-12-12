package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    ResponseBookingDto addBookingRequest(BookingDto bookingDto, Long userId);

    ResponseBookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    ResponseBookingDto getBooking(Long bookingId, Long userId);

    List<ResponseBookingDto> getBookings(Long userId, BookingState state);

    List<ResponseBookingDto> getBookingsOfAllItemsOfOwner(Long userId, BookingState state);
}
