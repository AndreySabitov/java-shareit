package ru.practicum.shareit.item;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody CreateItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByNameOrDescription(@PathParam("text") String text) {
        return itemService.getItemByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CreateCommentDto createCommentDto, @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получили text = {}, itemId = {}, userId = {}", createCommentDto.getText(), itemId, userId);
        return itemService.addComment(createCommentDto, itemId, userId);
    }
}
