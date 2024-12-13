package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestStorage requestStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user = User.builder()
            .id(1L)
            .name("name")
            .email("name@mail.ru")
            .build();
    private ItemRequest itemRequest = ItemRequest.builder()
            .created(LocalDateTime.now())
            .creator(user)
            .description("description")
            .id(1L)
            .build();

    @Test
    void testCanAddRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.save(any())).thenReturn(itemRequest);

        ItemRequestDto requestDto = ItemRequestMapper.mapToDto(itemRequest);
        ItemRequestDto requestDto1 = requestService.addRequest(CreateRequestDto.builder()
                .description("description").build(), user.getId());

        assertThat(requestDto.getId(), equalTo(requestDto1.getId()));
        assertThat(requestDto.getDescription(), equalTo(requestDto1.getDescription()));
    }

    @Test
    void testThrowNotFoundIfTryAddRequestWithUserNotExist() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addRequest(CreateRequestDto.builder()
                .description("description").build(), 1L));

        verify(requestStorage, never()).save(any());
    }

    @Test
    void testCanGetRequestById() {
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto requestDto = ItemRequestMapper.mapToDto(itemRequest);
        ItemRequestDto requestDto1 = requestService.getRequestById(itemRequest.getId());

        assertThat(requestDto.getId(), equalTo(requestDto1.getId()));
        assertThat(requestDto.getDescription(), equalTo(requestDto1.getDescription()));
    }

    @Test
    void testThrowNotFoundIfTryGetNotExistsRequest() {
        when(requestStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L));
    }

    @Test
    void testCanGetAllRequestsByCreator() {
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = List.of(itemRequest);

        when(userStorage.existsById(any())).thenReturn(true);
        when(requestStorage.findAllByCreatorId(user.getId(), newestFirst)).thenReturn(requests);

        List<ItemRequestDto> requestDtos = requests.stream().map(ItemRequestMapper::mapToDto).toList();
        List<ItemRequestDto> requestDtos1 = requestService.getAllByUser(user.getId());

        assertThat(requestDtos.size(), equalTo(requestDtos1.size()));
        assertThat(requestDtos.getFirst().getId(), equalTo(requestDtos1.getFirst().getId()));
    }

    @Test
    void testCanGetAllRequestsByCreatorWithResponses() {
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = List.of(itemRequest);
        Item item1 = Item.builder()
                .id(1L)
                .name("itemName1")
                .request(itemRequest)
                .owner(user)
                .available(true)
                .description("description1")
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("itemName2")
                .request(itemRequest)
                .owner(user)
                .available(true)
                .description("description2")
                .build();

        when(userStorage.existsById(any())).thenReturn(true);
        when(requestStorage.findAllByCreatorId(user.getId(), newestFirst)).thenReturn(requests);
        when(itemStorage.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(item1, item2));

        List<ItemRequestDto> requestDtos = requestService.getAllByUser(user.getId());

        assertThat(requestDtos.getFirst().getItems().size(), equalTo(2));
    }

    @Test
    void testThrowNotFoundIfTryGetAllRequestsByNotExistCreator() {
        assertThrows(NotFoundException.class, () -> requestService.getAllByUser(1L));
    }
}