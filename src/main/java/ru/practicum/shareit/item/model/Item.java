package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Integer ownerId;
    private Boolean available;
}
