package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item addItem(Item item) {
        userStorage.getUserById(item.getOwnerId());
        return itemStorage.addItem(item);
    }

    @Override
    public Item updateItem(Item item, Integer itemId) {
        Item oldItem = getItemById(itemId);
        if (!Objects.equals(oldItem.getOwnerId(), item.getOwnerId())) {
            throw new AuthorizationException("Нельзя обновить информацию о предмете другого пользователя");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return itemStorage.updateItem(oldItem, itemId);
    }

    @Override
    public Item getItemById(Integer itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<Item> getItemsOfUser(Integer userId) {
        userStorage.getUserById(userId);
        return itemStorage.getItemsOfUser(userId);
    }

    @Override
    public List<Item> getItemByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemStorage.getItemByNameOrDescription(text);
        }
    }
}
