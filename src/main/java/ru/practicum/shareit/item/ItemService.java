package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer userId, Integer itemId);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> getItemsOfUser(Integer userId);

    List<ItemDto> getItemByNameOrDescription(String text);
}
