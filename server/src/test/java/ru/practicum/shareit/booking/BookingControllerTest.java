package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("userName")
            .email("user@mail.ru")
            .build();
    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();
    private ResponseBookingDto response = ResponseBookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2024, 12, 9, 15, 0, 15))
            .end(LocalDateTime.of(2024, 12, 11, 15, 0, 15))
            .state(BookingState.CURRENT)
            .status(BookingStatus.APPROVED)
            .item(itemDto)
            .booker(userDto)
            .build();

    @Test
    void testAddBookingRequest() throws Exception {
        when(bookingService.addBookingRequest(any(), anyLong())).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(BookingDto.builder()
                                .start(LocalDateTime
                                        .of(2024, 12, 9, 15, 0, 15))
                                .end(LocalDateTime
                                        .of(2024, 12, 11, 15, 0, 15))
                                .itemId(1L)
                                .build()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(response.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(response.getEnd()))))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())));
    }

    @Test
    void testApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(response.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(response.getEnd()))))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())));
    }

    @Test
    void testGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(response.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(response.getEnd()))))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())));
    }

    @Test
    void testGetBookings() throws Exception {
        List<ResponseBookingDto> responses = List.of(response);

        when(bookingService.getBookings(anyLong(), any())).thenReturn(responses);

        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.booker.name", is(List.of(userDto.getName()))))
                .andExpect(jsonPath("$.*.item.name", is(List.of(itemDto.getName()))));
    }

    @Test
    void getBookingsOfItemsOfOwner() throws Exception {
        List<ResponseBookingDto> responses = List.of(response);

        when(bookingService.getBookingsOfAllItemsOfOwner(anyLong(), any())).thenReturn(responses);

        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.*.booker.name", is(List.of(userDto.getName()))))
                .andExpect(jsonPath("$.*.item.name", is(List.of(itemDto.getName()))));
    }
}