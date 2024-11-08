package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User getUserById(Integer userId);

    User createUser(User user);

    User updateUser(User user, Integer userId);

    User deleteUserById(Integer userId);
}
