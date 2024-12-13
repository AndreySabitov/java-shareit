package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingStorageTest {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private Sort sort;
    private User tenant;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void init() {
        sort = Sort.by(Sort.Direction.DESC, "start");
        owner = userStorage.save(User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build());
        tenant = userStorage.save(User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build());
        item = itemStorage.save(Item.builder()
                .owner(owner)
                .available(true)
                .description("description")
                .name("name")
                .build());
        booking = bookingStorage.save(Booking.builder()
                .tenant(tenant)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .item(item)
                .end(LocalDateTime.now().plusDays(1))
                .build());
    }

    @Test
    void testCanFindAllByTenantIdAndBetweenStartAndEnd() {
        assertThat(booking.getId(), equalTo(bookingStorage.findAllByTenantIdAndBetweenStartAndEnd(tenant.getId(),
                LocalDateTime.now(), sort).getFirst().getId()));
    }

    @Test
    void testCanFindAllByItemOwnerIdAndBetweenStartAndEnd() {
        assertThat(booking.getId(), equalTo(bookingStorage.findAllByItemOwnerIdAndBetweenStartAndEnd(owner.getId(),
                LocalDateTime.now(), sort).getFirst().getId()));
    }

    @Test
    void testCanFindAllByItemIdBeforeTime() {
        Booking booking1 = bookingStorage.save(Booking.builder()
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .tenant(tenant)
                .status(BookingStatus.APPROVED)
                .build());

        assertThat(booking1.getId(), equalTo(bookingStorage.findAllByItemIdBeforeTime(item.getId(),
                LocalDateTime.now()).getFirst().getId()));
    }

    @Test
    void testCanFindAllByItemIdAfterTime() {
        Booking booking1 = bookingStorage.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .tenant(tenant)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());

        assertThat(booking1.getId(), equalTo(bookingStorage.findAllByItemIdAfterTime(item.getId(),
                LocalDateTime.now()).getFirst().getId()));
    }
}