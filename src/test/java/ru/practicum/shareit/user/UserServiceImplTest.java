package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceImplTest {
    private UserService userService;

    @BeforeEach
    void initUserService() {
        this.userService = new UserServiceImpl(new InMemoryUserStorage());
    }

    @Test
    void testCanCreateUser() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        assertEquals("Name", user.getName());
    }

    @Test
    void testCanGetUserById() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        UserDto user1 = userService.getUserById(user.getId());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void testThrowNotFoundIfGetNotExistUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void testThrowDuplicateExceptionIfTryCreateUserWithSameEmail() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        assertThrows(DuplicateException.class, () -> userService.createUser(user));
    }

    @Test
    void testCanUpdateUser() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.updateUser(UpdateUserDto.builder().name("newName").email("newname@mail.ru").build(), user.getId());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals("newName", updatedUser.getName());
        assertEquals("newname@mail.ru", updatedUser.getEmail());
    }

    @Test
    void testCanUpdateUserName() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.updateUser(UpdateUserDto.builder().name("newName").build(), user.getId());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals("newName", updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    void testCanUpdateUserEmail() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.updateUser(UpdateUserDto.builder().email("newname@mail.ru").build(), user.getId());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals("newname@mail.ru", updatedUser.getEmail());
    }

    @Test
    void testCanDeleteUserById() {
        UserDto user = userService.createUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.deleteUserById(user.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void testThrowNotFoundIfTryDeleteNotExistenceUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }
}