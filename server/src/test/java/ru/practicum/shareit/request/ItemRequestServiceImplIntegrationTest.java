package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "jdbc.url=jdbc:postgresql://localhost:6541/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestService requestService;
    private final UserStorage userStorage;
    private final ItemRequestStorage requestStorage;

    private User user;
    private User otherUser;
    private ItemRequest itemRequest;
    private ItemRequest itemRequestOfOtherUser;

    @BeforeEach
    void init() {
        user = userStorage.save(User.builder()
                .name("name")
                .email("name@mail.ru")
                .build());
        otherUser = userStorage.save(User.builder()
                .name("otherName")
                .email("other@mail.ru")
                .build());
        itemRequest = requestStorage.save(ItemRequest.builder()
                .creator(user)
                .created(LocalDateTime.now())
                .description("description")
                .build());
        itemRequestOfOtherUser = requestStorage.save(ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("otherDescription")
                .creator(otherUser)
                .build());
    }

    @Test
    void testCanGetRequestsOfOtherUsers() {
        List<ItemRequestDto> requestDtoList = requestService.getRequestsOfOtherUsers(user.getId());
        ItemRequestDto requestDto = ItemRequestMapper.mapToDto(itemRequestOfOtherUser);

        assertThat(1, equalTo(requestDtoList.size()));
        assertThat(requestDto.getId(), equalTo(requestDtoList.getFirst().getId()));
        assertThat(requestDto.getDescription(), equalTo(requestDtoList.getFirst().getDescription()));
    }

    @Test
    void testThrowNotFoundIfTryGetRequestsOfOtherUsersWithNotExistUser() {
        Assertions.assertThrows(NotFoundException.class, () -> requestService.getRequestsOfOtherUsers(4L));
    }
}