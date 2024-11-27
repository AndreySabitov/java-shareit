package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.math.BigInteger;
import java.util.List;

public interface ItemStorage extends JpaRepository<Item, BigInteger> {

    @Query(value = "select it from Item as it where it.owner.id = ?1")
    List<Item> findByUserId(Integer userId);

    @Query(value = "select it from Item as it " +
            "where it.available = true " +
            "and lower(it.name) like %?1% or lower(it.description) like %?1%")
    List<Item> getItemByNameOrDescription(String text);
}
