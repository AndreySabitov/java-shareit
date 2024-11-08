package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@Valid @RequestBody UpdateUserDto userDto, @PathVariable Integer userId) {
        User user = UserMapper.updateUserDtoToUser(userDto);
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public User deleteUserById(@PathVariable Integer userId) {
        return userService.deleteUserById(userId);
    }
}
