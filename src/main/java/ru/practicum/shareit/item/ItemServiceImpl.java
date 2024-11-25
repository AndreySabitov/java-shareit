package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return ItemMapper.mapItemToItemDto(itemStorage.save(ItemMapper.mapItemDtoToItem(itemDto, user)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Integer userId, Integer itemId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item oldItem = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!Objects.equals(oldItem.getOwner().getId(), userId)) {
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
        return ItemMapper.mapItemToItemDto(itemStorage.save(oldItem));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.mapItemToItemDto(itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return itemStorage.findByUserId(userId).stream().map(ItemMapper::mapItemToItemDto).toList();
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemStorage.getItemByNameOrDescription(text.toLowerCase()).stream()
                    .map(ItemMapper::mapItemToItemDto)
                    .toList();
        }
    }
}
