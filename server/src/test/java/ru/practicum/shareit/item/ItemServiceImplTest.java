package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:6541/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserStorage userStorage;
    private User user;

    @BeforeEach
    void init() {
        user = userStorage.save(User.builder()
                .name("name")
                .email("name@mail.ru")
                .build());
    }

    @Test
    void testCanAddItem() {
        itemService.addItem(CreateItemDto.builder()
                .name("item").description("description").available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemsOfUser(user.getId());
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfAddItemWithNotExistUser() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(CreateItemDto.builder()
                .name("item").description("description").available(true).build(), 3L));
    }

    @Test
    void testCanUpdateItem() {
        Long id = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId()).getId();
        ItemDto updateItem = ItemDto.builder().name("updatedItem").description("updated description").available(false)
                .build();
        ItemDto updatedItem = itemService.updateItem(updateItem, user.getId(), id);
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testThrowAuthorizationExceptionIfTryUpdateItemWithOtherUserId() {
        User user1 = userStorage.save(User.builder()
                .name("name1")
                .email("name1@mail.ru")
                .build());
        Long id = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId()).getId();
        ItemDto updateItem = ItemDto.builder().name("updatedItem").description("updated description").available(false)
                .build();
        assertThrows(AuthorizationException.class, () -> itemService.updateItem(updateItem, user1.getId(), id));
    }

    @Test
    void testCanUpdateName() {
        ItemDto item = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        ItemDto updateItem = ItemDto.builder().name("updatedItem").build();
        ItemDto updatedItem = itemService.updateItem(updateItem, user.getId(), item.getId());
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateDescription() {
        ItemDto item = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        ItemDto updateItem = ItemDto.builder().description("updated description").build();
        ItemDto updatedItem = itemService.updateItem(updateItem, user.getId(), item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateAvailable() {
        ItemDto item = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        ItemDto updateItem = ItemDto.builder().available(false).build();
        ItemDto updatedItem = itemService.updateItem(updateItem, user.getId(), item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testCanGetItemById() {
        ItemDto item1 = itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        ItemDto item2 = itemService.getItemById(item1.getId());
        assertEquals(item1.getName(), item2.getName());
    }

    @Test
    void testThrowNotFoundIfTryGetNotExistenceItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L));
    }

    @Test
    void testCanGetItemsOfUser() {
        itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemsOfUser(user.getId());
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfTryGetItemsOfNotExistenceUser() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsOfUser(3L));
    }

    @Test
    void testCanGetItemByName() {
        itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemByNameOrDescription("ite");
        assertEquals(1, items.size());
    }

    @Test
    void testCanGetItemByDescription() {
        itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemByNameOrDescription("escr");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchNotDependsOfLetterCase() {
        itemService.addItem(CreateItemDto.builder().name("item").description("dEsCription")
                .available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemByNameOrDescription("eScR");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchGetOnlyAvailableItems() {
        itemService.addItem(CreateItemDto.builder().name("item1").description("description1").available(true)
                .build(), user.getId());
        itemService.addItem(CreateItemDto.builder().name("item2").description("description2").available(false)
                .build(), user.getId());
        List<ItemDto> items = itemService.getItemByNameOrDescription("item");
        assertEquals(1, items.size());
    }

    @Test
    void testGetEmptyListIfTextIsBlank() {
        itemService.addItem(CreateItemDto.builder().name("item").description("description")
                .available(true).build(), user.getId());
        List<ItemDto> items = itemService.getItemByNameOrDescription("");
        assertTrue(items.isEmpty());
    }
}