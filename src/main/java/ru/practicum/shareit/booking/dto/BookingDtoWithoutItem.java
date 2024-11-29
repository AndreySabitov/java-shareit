package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingDtoWithoutItem {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState state;
    private BookingStatus status;
    private User booker;
}
