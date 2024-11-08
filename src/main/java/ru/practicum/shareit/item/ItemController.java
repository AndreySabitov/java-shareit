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
    public Item addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        Item item = ItemMapper.ItemDtoToItem(itemDto, userId);
        return itemService.addItem(item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId,
                           @PathVariable Integer itemId) {
        Item item = ItemMapper.ItemDtoToItem(itemDto, userId);
        return itemService.updateItem(item, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Integer itemId,
                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<Item> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<Item> getItemByNameOrDescription(@PathParam("text") String text,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemByNameOrDescription(text);
    }
}
