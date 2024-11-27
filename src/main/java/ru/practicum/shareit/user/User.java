package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private BigInteger id;
    private String name;
    private String email;
}
