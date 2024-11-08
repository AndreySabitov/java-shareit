package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item ItemDtoToItem(ItemDto itemDto, Integer userId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .ownerId(userId)
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto ItemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
