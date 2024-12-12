package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;

    ItemDto itemDto = ItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .id(1L)
            .build();

    @Test
    void testAddItem() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto updatedItem = ItemDto.builder()
                .id(1L)
                .available(true)
                .name("newName")
                .description("description")
                .build();

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(updatedItem);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ItemDto.builder().name("newName").build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemById(anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void testGetItemsOfUser() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.getItemsOfUser(anyLong())).thenReturn(items);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.name", is(List.of(itemDto.getName()))));
    }

    @Test
    void testGetItemByNameOrDescription() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.getItemByNameOrDescription(anyString())).thenReturn(items);

        mvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.name", is(List.of(itemDto.getName()))));
    }

    @Test
    void testAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .created(LocalDateTime.of(2024, 12, 10, 15, 0, 15))
                .authorName("Author")
                .text("some text")
                .id(1L)
                .build();

        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(CreateCommentDto.builder().text("some text").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(String.valueOf(commentDto.getCreated()))));
    }
}