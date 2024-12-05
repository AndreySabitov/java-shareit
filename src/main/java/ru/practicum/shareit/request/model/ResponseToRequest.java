package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Entity
@Table(name = "request_responses")
@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
public class ResponseToRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
