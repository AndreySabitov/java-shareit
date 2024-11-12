package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(Integer userId) {
        return UserMapper.mapUserToUserDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapUserDtoToUser(userDto);
        validateUser(user);
        return UserMapper.mapUserToUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(UpdateUserDto userDto, Integer userId) {
        User oldUser = userStorage.getUserById(userId);
        User user = UserMapper.mapUpdateUserDtoToUser(userDto);
        validateUser(user);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.mapUserToUserDto(userStorage.updateUser(oldUser));
    }

    @Override
    public void deleteUserById(Integer userId) {
        userStorage.getUserById(userId);
        userStorage.deleteUserById(userId);
    }

    private void validateUser(User user) {
        if (user.getEmail() != null) {
            if (userStorage.getUsers().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
                throw new DuplicateException("Такой email уже существует");
            }
        }
    }
}
