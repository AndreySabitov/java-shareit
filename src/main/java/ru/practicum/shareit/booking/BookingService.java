package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.math.BigInteger;
import java.util.List;

public interface BookingService {
    ResponseBookingDto addBookingRequest(BookingDto bookingDto, BigInteger userId);

    ResponseBookingDto approveBooking(BigInteger bookingId, BigInteger userId, Boolean approved);

    ResponseBookingDto getBooking(BigInteger bookingId, BigInteger userId);

    List<ResponseBookingDto> getBookings(BigInteger userId, BookingState state);

    List<ResponseBookingDto> getBookingsOfAllItemsOfOwner(BigInteger userId, BookingState state);
}
