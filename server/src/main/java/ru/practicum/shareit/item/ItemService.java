package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(CreateItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsOfUser(Long userId);

    List<ItemDto> getItemByNameOrDescription(String text);

    CommentDto addComment(CreateCommentDto createCommentDto, Long itemId, Long userId);
}
