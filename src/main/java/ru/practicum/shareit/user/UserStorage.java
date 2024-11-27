package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserStorage extends JpaRepository<User, BigInteger> {
    boolean existsByEmail(String email);
}
