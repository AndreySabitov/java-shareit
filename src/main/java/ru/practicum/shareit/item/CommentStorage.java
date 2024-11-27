package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.math.BigInteger;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, BigInteger> {
    @Query(value = "select com from Comment as com where com.item.id = ?1")
    List<Comment> findAllByItemId(BigInteger itemId);
}
