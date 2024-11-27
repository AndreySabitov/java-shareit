package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigInteger id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private final Set<CommentDto> comments = new HashSet<>();
    private BookingDtoWithoutItem lastBooking;
    private BookingDtoWithoutItem nextBooking;
}
