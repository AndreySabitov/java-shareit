package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        Item item = itemService.addItem(ItemMapper.mapItemDtoToItem(itemDto, userId));
        return ItemMapper.mapItemToItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId) {
        Item item = itemService.updateItem(ItemMapper.mapItemDtoToItem(itemDto, userId), itemId);
        return ItemMapper.mapItemToItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId,
                               @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ItemMapper.mapItemToItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsOfUser(userId).stream().map(ItemMapper::mapItemToItemDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByNameOrDescription(@PathParam("text") String text,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemByNameOrDescription(text).stream().map(ItemMapper::mapItemToItemDto).toList();
    }
}
