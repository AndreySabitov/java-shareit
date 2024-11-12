package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Integer itemId);

    List<Item> getItemsOfUser(Integer userId);

    List<Item> getItemByNameOrDescription(String text);
}
