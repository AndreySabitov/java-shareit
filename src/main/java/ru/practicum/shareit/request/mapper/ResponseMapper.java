package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ResponseToRequest;
import ru.practicum.shareit.user.User;

@UtilityClass
public class ResponseMapper {
    public ResponseToRequest mapToResponse(ItemRequest request, Item item, User user) {
        return ResponseToRequest.builder()
                .itemRequest(request)
                .item(item)
                .name(item.getName())
                .owner(user)
                .build();
    }

    public ResponseDto mapToDto(ResponseToRequest response) {
        return ResponseDto.builder()
                .item_id(response.getItem().getId())
                .name(response.getName())
                .owner_id(response.getOwner().getId())
                .build();
    }
}
