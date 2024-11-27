package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public final class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, User user, Item item, BookingStatus status) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .tenant(user)
                .item(item)
                .status(status)
                .build();
    }

    public static ResponseBookingDto mapToResponse(Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .state(calcState(booking))
                .booker(booking.getTenant())
                .item(booking.getItem())
                .build();
    }

    public static BookingDtoWithoutItem mapToWithoutItemDto(Booking booking) {
        return BookingDtoWithoutItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .state(calcState(booking))
                .booker(booking.getTenant())
                .build();
    }

    private static BookingState calcState(Booking booking) {
        BookingStatus status = booking.getStatus();
        LocalDateTime currentDate = LocalDateTime.now();
        if (status == BookingStatus.REJECTED) {
            return BookingState.REJECTED;
        } else if (status == BookingStatus.WAITING) {
            return BookingState.WAITING;
        } else {
            if (currentDate.isBefore(booking.getStart())) {
                return BookingState.FUTURE;
            } else if (currentDate.isAfter(booking.getEnd())) {
                return BookingState.PAST;
            } else {
                return BookingState.CURRENT;
            }
        }
    }
}
