package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> itemStorage = new HashMap<>();
    int count = 0;

    @Override
    public Item addItem(Item item) {
        int id = generateId();
        item.setId(id);
        itemStorage.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Item item, Integer itemId) {
        return itemStorage.replace(itemId, item);
    }

    @Override
    public Item getItemById(Integer itemId) {
        if (itemStorage.containsKey(itemId)) {
            return itemStorage.get(itemId);
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public List<Item> getItemsOfUser(Integer userId) {
        return new ArrayList<>(itemStorage.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList());
    }

    @Override
    public List<Item> getItemByNameOrDescription(String text) {
        return new ArrayList<>(itemStorage.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .toList());
    }

    private int generateId() {
        return ++count;
    }
}
