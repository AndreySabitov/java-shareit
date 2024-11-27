package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.math.BigInteger;
import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, BigInteger userId);

    ItemDto updateItem(ItemDto itemDto, BigInteger userId, BigInteger itemId);

    ItemDto getItemById(BigInteger itemId);

    List<ItemDto> getItemsOfUser(BigInteger userId);

    List<ItemDto> getItemByNameOrDescription(String text);

    CommentDto addComment(CreateCommentDto createCommentDto, BigInteger itemId, BigInteger userId);
}
