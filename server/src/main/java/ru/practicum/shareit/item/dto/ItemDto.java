package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private final Set<CommentDto> comments = new HashSet<>();
    private BookingDtoWithoutItem lastBooking;
    private BookingDtoWithoutItem nextBooking;
}
