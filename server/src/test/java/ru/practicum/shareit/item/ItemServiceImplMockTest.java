package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplMockTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage requestStorage;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner = User.builder()
            .id(1L)
            .name("name")
            .email("name@mail.ru")
            .build();
    private User tenant = User.builder()
            .id(2L)
            .name("tenantName")
            .email("tenant@mail.ru")
            .build();
    private Item item = Item.builder()
            .id(1L)
            .name("itemName")
            .description("description")
            .available(true)
            .owner(owner)
            .build();
    private Booking booking = Booking.builder()
            .item(item)
            .tenant(tenant)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .status(BookingStatus.APPROVED)
            .id(1L)
            .build();
    private Comment comment = Comment.builder()
            .created(LocalDateTime.now())
            .item(item)
            .text("Text")
            .author(tenant)
            .id(1L)
            .build();
    private ItemRequest request = ItemRequest.builder()
            .creator(tenant)
            .description("description")
            .created(LocalDateTime.now())
            .id(1L)
            .build();

    @Test
    void testCanAddComment() {
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(bookingStorage
                .findByTenantIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(commentStorage.save(any())).thenReturn(comment);

        CommentDto commentDto = CommentMapper.mapToDto(comment);
        CommentDto commentDto1 = itemService
                .addComment(CreateCommentDto.builder().text("Text").build(), item.getId(), tenant.getId());

        assertThat(commentDto.getId(), equalTo(commentDto1.getId()));
        assertThat(commentDto.getText(), equalTo(commentDto1.getText()));
        assertThat(commentDto.getAuthorName(), equalTo(commentDto1.getAuthorName()));
    }

    @Test
    void testThrowNotFoundIfTryAddCommentToNotExistItem() {
        assertThrows(NotFoundException.class, () -> itemService
                .addComment(CreateCommentDto.builder().text("Text").build(), item.getId(), tenant.getId()));
    }

    @Test
    void testThrowNotFoundIfNotExistUserTryAddComment() {
        when(itemStorage.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> itemService
                .addComment(CreateCommentDto.builder().text("Text").build(), item.getId(), tenant.getId()));
    }

    @Test
    void testThrowValidationExceptionIfTryAddCommentAndBookingNotApproved() {
        booking.setStatus(BookingStatus.WAITING);

        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(userStorage.existsById(anyLong())).thenReturn(true);
        when(bookingStorage
                .findByTenantIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException());

        assertThrows(ValidationException.class, () -> itemService
                .addComment(CreateCommentDto.builder().text("Text").build(), item.getId(), tenant.getId()));
    }

    @Test
    void testCanAddItemWithRequestId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemStorage.save(any())).thenReturn(item);

        ItemDto itemDto = itemService.addItem(CreateItemDto.builder()
                .requestId(1L)
                .available(true)
                .description("description")
                .name("itemName")
                .build(), owner.getId());

        assertThat("itemName", equalTo(itemDto.getName()));
    }

    @Test
    void testThrowNotFoundIfTryAddItemWithNotExistRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(CreateItemDto.builder()
                .requestId(1L)
                .available(true)
                .description("description")
                .name("name")
                .build(), owner.getId()));
    }
}