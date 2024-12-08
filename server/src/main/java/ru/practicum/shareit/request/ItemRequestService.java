package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(CreateRequestDto createRequestDto, Long userId);

    List<ItemRequestDto> getAllByUser(Long userId);

    List<ItemRequestDto> getRequestsOfOtherUsers(Long userId);

    ItemRequestDto getRequestById(Long requestId);
}
