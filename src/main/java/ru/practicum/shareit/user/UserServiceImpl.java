package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(BigInteger userId) {
        return UserMapper.mapUserToUserDto(userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapUserDtoToUser(userDto);
        validateUser(user);
        return UserMapper.mapUserToUserDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserDto userDto, BigInteger userId) {
        User oldUser = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User user = UserMapper.mapUpdateUserDtoToUser(userDto);
        validateUser(user);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.mapUserToUserDto(userStorage.save(oldUser));
    }

    @Override
    public void deleteUserById(BigInteger userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        userStorage.deleteById(userId);
    }

    private void validateUser(User user) {
        if (user.getEmail() != null) {
            if (userStorage.existsByEmail(user.getEmail())) {
                throw new DuplicateException("email уже используется");
            }
        }
    }
}
