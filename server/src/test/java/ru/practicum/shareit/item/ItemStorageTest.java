package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemStorageTest {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    void testCanGetItemByNameOrDescription() {
        User user = userStorage.save(User.builder()
                .name("user")
                .email("user@mail.ri")
                .build());

        Item item = itemStorage.save(Item.builder()
                .name("name")
                .description("Description")
                .available(true)
                .owner(user)
                .build());

        assertThat(item.getName(), equalTo(itemStorage.getItemByNameOrDescription("escr").getFirst().getName()));
    }
}