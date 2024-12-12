package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String name;
    private String email;
}
