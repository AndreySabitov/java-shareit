package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.math.BigInteger;
import java.util.List;

public interface BookingService {
    ResponseBookingDto addBookingRequest(BookingDto bookingDto, Integer userId);

    ResponseBookingDto approveBooking(BigInteger bookingId, Integer userId, Boolean approved);

    ResponseBookingDto getBooking(BigInteger bookingId, Integer userId);

    List<ResponseBookingDto> getBookings(Integer userId, BookingState state);

    List<ResponseBookingDto> getBookingsOfAllItemsOfOwner(Integer userId, BookingState state);
}
