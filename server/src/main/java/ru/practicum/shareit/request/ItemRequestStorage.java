package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByCreatorId(Long userId, Sort sort);

    List<ItemRequest> findAllByCreatorIdNot(Long userId, Sort sort);
}
