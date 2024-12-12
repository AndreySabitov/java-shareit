package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<ItemDto> jacksonTester;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(BookingDtoWithoutItem.builder()
                        .id(1L)
                        .booker(UserDto.builder().id(1L).name("booker").email("booker@mail.ru").build())
                        .start(LocalDateTime.of(2024, 12, 9, 14, 30, 30))
                        .end(LocalDateTime.of(2024, 12, 10, 14, 30, 30))
                        .state(BookingState.PAST)
                        .status(BookingStatus.APPROVED)
                        .build())
                .nextBooking(BookingDtoWithoutItem.builder()
                        .id(2L)
                        .booker(UserDto.builder().id(2L).name("booker2").email("booker2@mail.ru").build())
                        .start(LocalDateTime.of(2024, 12, 12, 14, 30, 30))
                        .end(LocalDateTime.of(2024, 12, 13, 14, 30, 30))
                        .state(BookingState.FUTURE)
                        .status(BookingStatus.APPROVED)
                        .build())
                .build();

        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo("2024-12-09T14:30:30");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo("2024-12-13T14:30:30");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.state").isEqualTo("PAST");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status")
                .isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.email")
                .isEqualTo("booker@mail.ru");
    }
}