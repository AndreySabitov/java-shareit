package ru.practicum.shareit.user;

public interface UserService {
    User getUserById(Integer userId);

    User createUser(User user);

    User updateUser(User user, Integer userId);

    User deleteUserById(Integer userId);
}
