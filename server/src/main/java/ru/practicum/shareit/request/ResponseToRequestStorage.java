package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ResponseToRequest;

import java.util.Set;

public interface ResponseToRequestStorage extends JpaRepository<ResponseToRequest, Long> {
    Set<ResponseToRequest> findAllByItemRequestId(Long requestId);
}
