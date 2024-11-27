package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ItemMapper {

    public static Item mapItemDtoToItem(ItemDto itemDto, User user) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto mapItemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
