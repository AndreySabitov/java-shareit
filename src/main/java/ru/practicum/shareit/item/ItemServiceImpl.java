package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        userStorage.getUserById(userId);
        return ItemMapper.mapItemToItemDto(itemStorage.addItem(ItemMapper.mapItemDtoToItem(itemDto, userId)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer userId, Integer itemId) {
        userStorage.getUserById(userId);
        Item oldItem = itemStorage.getItemById(itemId);
        if (!Objects.equals(oldItem.getOwnerId(), userId)) {
            throw new AuthorizationException("Нельзя обновить информацию о предмете другого пользователя");
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapItemToItemDto(itemStorage.updateItem(oldItem));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapItemToItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        userStorage.getUserById(userId);
        return itemStorage.getItemsOfUser(userId).stream().map(ItemMapper::mapItemToItemDto).toList();
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemStorage.getItemByNameOrDescription(text).stream().map(ItemMapper::mapItemToItemDto).toList();
        }
    }
}
