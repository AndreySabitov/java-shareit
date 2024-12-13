package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest mapToRequest(CreateRequestDto requestDto, User creator) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .creator(creator)
                .build();
    }

    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
