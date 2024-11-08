package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer count = 0;


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUserById(Integer userId) {
        if (userStorage.containsKey(userId)) {
            return userStorage.get(userId);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User createUser(User user) {
        int id = generateId();
        user.setId(id);
        userStorage.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user, Integer userId) {
        userStorage.replace(userId, user);
        return user;
    }

    @Override
    public User deleteUserById(Integer userId) {
        User user = getUserById(userId);
        userStorage.remove(userId);
        return user;
    }

    private Integer generateId() {
        return ++count;
    }
}
