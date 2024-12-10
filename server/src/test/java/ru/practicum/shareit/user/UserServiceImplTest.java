package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:6541/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

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
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
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
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }
}