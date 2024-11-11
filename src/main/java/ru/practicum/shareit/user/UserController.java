package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return UserMapper.mapUserToUserDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(UserMapper.mapUserDtoToUser(userDto));
        return UserMapper.mapUserToUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody UpdateUserDto userDto, @PathVariable Integer userId) {
        User user = UserMapper.mapUpdateUserDtoToUser(userDto);
        return UserMapper.mapUserToUserDto(userService.updateUser(user, userId));
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUserById(@PathVariable Integer userId) {
        return UserMapper.mapUserToUserDto(userService.deleteUserById(userId));
    }
}
