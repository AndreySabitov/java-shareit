package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplTest {
    private ItemService itemService;

    @BeforeEach
    void initItemService() {
        UserStorage userStorage = new InMemoryUserStorage();
        userStorage.createUser(User.builder().name("Name1").email("name1@mail.ru").build());
        userStorage.createUser(User.builder().name("Name2").email("name2@mail.ru").build());
        this.itemService = new ItemServiceImpl(new InMemoryItemStorage(), userStorage);
    }

    @Test
    void testCanAddItem() {
        itemService.addItem(ItemDto.builder()
                .name("item").description("description").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemsOfUser(1);
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfAddItemWithNotExistUser() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(ItemDto.builder()
                .name("item").description("description").available(true).build(), 3));
    }

    @Test
    void testCanUpdateItem() {
        int id = itemService.addItem(ItemDto.builder().name("item").description("description")
                .available(true).build(), 1).getId();
        ItemDto updateItem = ItemDto.builder().name("updatedItem").description("updated description").available(false)
                .build();
        ItemDto updatedItem = itemService.updateItem(updateItem, 1, id);
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testThrowAuthorizationExceptionIfTryUpdateItemWithOtherUserId() {
        int id = itemService.addItem(ItemDto.builder().name("item").description("description")
                .available(true).build(), 1).getId();
        ItemDto updateItem = ItemDto.builder().name("updatedItem").description("updated description").available(false)
                .build();
        assertThrows(AuthorizationException.class, () -> itemService.updateItem(updateItem, 2, id));
    }

    @Test
    void testCanUpdateName() {
        ItemDto item = itemService.addItem(ItemDto.builder().name("item").description("description").available(true)
                .build(), 1);
        ItemDto updateItem = ItemDto.builder().name("updatedItem").build();
        ItemDto updatedItem = itemService.updateItem(updateItem, 1, item.getId());
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateDescription() {
        ItemDto item = itemService.addItem(ItemDto.builder().name("item").description("description").available(true)
                .build(), 1);
        ItemDto updateItem = ItemDto.builder().description("updated description").build();
        ItemDto updatedItem = itemService.updateItem(updateItem, 1, item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateAvailable() {
        ItemDto item = itemService.addItem(ItemDto.builder().name("item").description("description").available(true)
                .build(), 1);
        ItemDto updateItem = ItemDto.builder().available(false).build();
        ItemDto updatedItem = itemService.updateItem(updateItem, 1, item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testCanGetItemById() {
        ItemDto item1 = itemService.addItem(ItemDto.builder().name("item").description("description").available(true)
                .build(), 1);
        ItemDto item2 = itemService.getItemById(item1.getId());
        assertEquals(item1.getName(), item2.getName());
    }

    @Test
    void testThrowNotFoundIfTryGetNotExistenceItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1));
    }

    @Test
    void testCanGetItemsOfUser() {
        itemService.addItem(ItemDto.builder().name("item").description("description").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemsOfUser(1);
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfTryGetItemsOfNotExistenceUser() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsOfUser(3));
    }

    @Test
    void testCanGetItemByName() {
        itemService.addItem(ItemDto.builder().name("item").description("description").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemByNameOrDescription("ite");
        assertEquals(1, items.size());
    }

    @Test
    void testCanGetItemByDescription() {
        itemService.addItem(ItemDto.builder().name("item").description("description").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemByNameOrDescription("escr");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchNotDependsOfLetterCase() {
        itemService.addItem(ItemDto.builder().name("item").description("dEsCription").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemByNameOrDescription("eScR");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchGetOnlyAvailableItems() {
        itemService.addItem(ItemDto.builder().name("item1").description("description1").available(true)
                .build(), 1);
        itemService.addItem(ItemDto.builder().name("item2").description("description2").available(false)
                .build(), 1);
        List<ItemDto> items = itemService.getItemByNameOrDescription("item");
        assertEquals(1, items.size());
    }

    @Test
    void testGetEmptyListIfTextIsBlank() {
        itemService.addItem(ItemDto.builder().name("item").description("description").available(true).build(), 1);
        List<ItemDto> items = itemService.getItemByNameOrDescription("");
        assertTrue(items.isEmpty());
    }
}