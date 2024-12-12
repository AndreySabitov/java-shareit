package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage requestStorage;

    @Override
    @Transactional
    public ItemDto addItem(CreateItemDto itemDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = requestStorage.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }
        Item item = itemStorage.save(ItemMapper.mapCreateDtoToItem(itemDto, user, itemRequest));
        return ItemMapper.mapItemToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
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
    public ItemDto getItemById(Long itemId) {
        ItemDto itemDto = ItemMapper.mapItemToItemDto(itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));
        setComments(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsOfUser(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ItemDto> items = itemStorage.findByOwnerId(userId).stream().map(ItemMapper::mapItemToItemDto).toList();
        items.forEach(this::setFields);
        return items;
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

    @Override
    @Transactional
    public CommentDto addComment(CreateCommentDto createCommentDto, Long itemId, Long userId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("Вещь с таким id не найдена");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        try {
            log.info("в системе сейчас {}", LocalDateTime.now());
            Booking booking = bookingStorage.findByTenantIdAndItemIdAndStatusAndEndBefore(userId, itemId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            log.info("нашли бронирование № {}", booking.getId());
            return CommentMapper.mapToDto(commentStorage
                    .save(CommentMapper.mapToComment(createCommentDto, booking.getItem(), booking.getTenant())));
        } catch (Exception e) {
            throw new ValidationException("бронирование не найдено");
        }
    }

    private void setComments(ItemDto itemDto) {
        itemDto.getComments().addAll(commentStorage.findAllByItemId(itemDto.getId()).stream()
                .map(CommentMapper::mapToDto).toList());
    }

    private void setFields(ItemDto itemDto) {
        setComments(itemDto);

        BookingDtoWithoutItem lastBooking = bookingStorage
                .findAllByItemIdBeforeTime(itemDto.getId(), LocalDateTime.now()).stream()
                .map(BookingMapper::mapToWithoutItemDto).findFirst().orElse(null);
        if (lastBooking == null) {
            log.info("прошлых бронирований не найдено");
        }
        itemDto.setLastBooking(lastBooking);

        BookingDtoWithoutItem nextBooking = bookingStorage
                .findAllByItemIdAfterTime(itemDto.getId(), LocalDateTime.now()).stream()
                .map(BookingMapper::mapToWithoutItemDto).findFirst().orElse(null);
        if (nextBooking == null) {
            log.info("будущих бронирований не найдено");
        }
        itemDto.setNextBooking(nextBooking);
    }
}
