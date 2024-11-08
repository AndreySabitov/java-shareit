package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user, Integer userId) {
        User oldUser = getUserById(userId);
        validateUser(user);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return userStorage.updateUser(oldUser, userId);
    }

    @Override
    public User deleteUserById(Integer userId) {
        return userStorage.deleteUserById(userId);
    }

    private void validateUser(User user) {
        if (userStorage.getUsers().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new DuplicateException("Такой email уже существует");
        }
    }
}
