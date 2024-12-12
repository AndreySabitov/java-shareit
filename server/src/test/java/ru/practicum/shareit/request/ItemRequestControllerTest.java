package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto requestDto = ItemRequestDto.builder()
            .created(LocalDateTime.of(2024, 12, 10, 11, 0, 40))
            .description("description")
            .id(1L)
            .build();

    @Test
    void testAddRequest() throws Exception {
        CreateRequestDto createRequestDto = CreateRequestDto.builder()
                .description("description")
                .build();

        when(itemRequestService.addRequest(any(), anyLong())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(requestDto.getCreated()))));

    }

    @Test
    void testGetAllByUser() throws Exception {
        List<ItemRequestDto> requestDtoList = List.of(requestDto);

        when(itemRequestService.getAllByUser(anyLong())).thenReturn(requestDtoList);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.description", is(List.of(requestDto.getDescription()))));

        verify(itemRequestService, times(1)).getAllByUser(anyLong());
    }

    @Test
    void testGetAllOfOtherUsers() throws Exception {
        List<ItemRequestDto> requestDtoList = List.of(requestDto);

        when(itemRequestService.getRequestsOfOtherUsers(anyLong())).thenReturn(requestDtoList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.description", is(List.of(requestDto.getDescription()))));
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getRequestById(anyLong())).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(requestDto.getCreated()))));
    }
}