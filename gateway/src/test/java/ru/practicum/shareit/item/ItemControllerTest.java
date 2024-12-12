package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemClient itemClient;

    @Test
    void testReturnBadRequestIfTryCreateItemWithoutName() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .available(true)
                .description("description")
                .build();

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(any(), anyLong());
    }

    @Test
    void testReturnBadRequestIfTryCreateItemWithNameIsBlank() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name(" ")
                .available(true)
                .description("description")
                .build();

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(any(), anyLong());
    }

    @Test
    void testReturnBadRequestIfTryCreateItemWithoutDescription() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name("name")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(any(), anyLong());
    }

    @Test
    void testReturnBadRequestIfTryCreateItemWithDescriptionIsBlank() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name("name")
                .available(true)
                .description(" ")
                .build();

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(any(), anyLong());
    }

    @Test
    void testReturnBadRequestIfTryCreateItemWithoutAvailable() throws Exception {
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name("name")
                .description("description")
                .build();

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(any(), anyLong());
    }

    @Test
    void testReturnBadRequestIfTryAddCommentWithoutText() throws Exception {
        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .text(null)
                .build();

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createCommentDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(any(), anyLong(), anyLong());
    }
}