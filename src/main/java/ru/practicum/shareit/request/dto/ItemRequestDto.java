package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private final Set<ResponseDto> items = new HashSet<>();
}
