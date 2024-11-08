package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull(message = "email должен быть задан")
    @Email(message = "Неверный формат email")
    private String email;
}
