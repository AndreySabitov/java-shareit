package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.AuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplTest {
    private ItemService itemService;
    private final UserStorage userStorage = new InMemoryUserStorage();

    @BeforeEach
    void initItemService() {
        userStorage.createUser(User.builder().name("Name1").email("name1@mail.ru").build());
        userStorage.createUser(User.builder().name("Name2").email("name2@mail.ru").build());
        this.itemService = new ItemServiceImpl(new InMemoryItemStorage(), userStorage);
    }

    @Test
    void testCanAddItem() {
        itemService.addItem(Item.builder()
                .name("item").description("description").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemsOfUser(1);
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfAddItemWithNotExistUser() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(Item.builder()
                .name("item").description("description").available(true).ownerId(3).build()));
    }

    @Test
    void testCanUpdateItem() {
        Item item = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item updateItem = Item.builder().name("updatedItem").description("updated description").available(false)
                .ownerId(1).build();
        Item updatedItem = itemService.updateItem(updateItem, item.getId());
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testThrowAuthorizationExceptionIfTryUpdateItemWithOtherUserId() {
        Item item = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item updateItem = Item.builder().name("updatedItem").description("updated description").available(false)
                .ownerId(2).build();
        assertThrows(AuthorizationException.class, () -> itemService.updateItem(updateItem, item.getId()));
    }

    @Test
    void testCanUpdateName() {
        Item item = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item updateItem = Item.builder().name("updatedItem").ownerId(1).build();
        Item updatedItem = itemService.updateItem(updateItem, item.getId());
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateDescription() {
        Item item = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item updateItem = Item.builder().description("updated description").ownerId(1).build();
        Item updatedItem = itemService.updateItem(updateItem, item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    void testCanUpdateAvailable() {
        Item item = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item updateItem = Item.builder().available(false).ownerId(1).build();
        Item updatedItem = itemService.updateItem(updateItem, item.getId());
        assertEquals(updatedItem.getName(), item.getName());
        assertEquals(updatedItem.getDescription(), item.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());
    }

    @Test
    void testCanGetItemById() {
        Item item1 = itemService.addItem(Item.builder().name("item").description("description").available(true)
                .ownerId(1).build());
        Item item2 = itemService.getItemById(item1.getId());
        assertEquals(item1.getName(), item2.getName());
    }

    @Test
    void testThrowNotFoundIfTryGetNotExistenceItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1));
    }

    @Test
    void testCanGetItemsOfUser() {
        itemService.addItem(Item.builder().name("item").description("description").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemsOfUser(1);
        assertEquals(1, items.size());
    }

    @Test
    void testThrowNotFoundIfTryGetItemsOfNotExistenceUser() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsOfUser(3));
    }

    @Test
    void testCanGetItemByName() {
        itemService.addItem(Item.builder().name("item").description("description").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemByNameOrDescription("ite");
        assertEquals(1, items.size());
    }

    @Test
    void testCanGetItemByDescription() {
        itemService.addItem(Item.builder().name("item").description("description").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemByNameOrDescription("escr");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchNotDependsOfLetterCase() {
        itemService.addItem(Item.builder().name("item").description("dEsCription").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemByNameOrDescription("eScR");
        assertEquals(1, items.size());
    }

    @Test
    void testSearchGetOnlyAvailableItems() {
        itemService.addItem(Item.builder().name("item1").description("description1").available(true)
                .ownerId(1).build());
        itemService.addItem(Item.builder().name("item2").description("description2").available(false)
                .ownerId(1).build());
        List<Item> items = itemService.getItemByNameOrDescription("item");
        assertEquals(1, items.size());
    }

    @Test
    void testGetEmptyListIfTextIsBlank() {
        itemService.addItem(Item.builder().name("item").description("description").available(true).ownerId(1).build());
        List<Item> items = itemService.getItemByNameOrDescription("");
        assertTrue(items.isEmpty());
    }
}