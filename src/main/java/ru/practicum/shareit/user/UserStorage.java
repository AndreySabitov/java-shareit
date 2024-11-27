package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStorage extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
}
