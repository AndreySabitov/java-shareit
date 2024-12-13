package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> addItem(CreateItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long userId, Long itemId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsOfUser(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemByNameOrDescription(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(CreateCommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
