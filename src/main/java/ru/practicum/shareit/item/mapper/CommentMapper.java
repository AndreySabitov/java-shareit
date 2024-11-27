package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public final class CommentMapper {
    public static Comment mapToComment(CreateCommentDto createCommentDto, Item item, User user) {
        return Comment.builder()
                .text(createCommentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
