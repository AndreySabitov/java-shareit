package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDto {
    private BigInteger id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
