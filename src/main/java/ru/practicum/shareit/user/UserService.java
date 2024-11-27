package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.math.BigInteger;

public interface UserService {
    UserDto getUserById(BigInteger userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UpdateUserDto userDto, BigInteger userId);

    void deleteUserById(BigInteger userId);
}
