package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage requestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public ItemRequestDto addRequest(CreateRequestDto createRequestDto, Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return ItemRequestMapper.mapToDto(requestStorage.save(ItemRequestMapper.mapToRequest(createRequestDto, user)));
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequestDto> requests = requestStorage.findAllByCreatorId(userId, newestFirst).stream()
                .map(ItemRequestMapper::mapToDto).toList();
        setItems(requests);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getRequestsOfOtherUsers(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        return requestStorage.findAllByCreatorIdNot(userId, newestFirst).stream()
                .map(ItemRequestMapper::mapToDto).toList();
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequestDto requestDto = ItemRequestMapper.mapToDto(requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found")));
        List<ResponseDto> items = itemStorage.findAllByRequestId(requestId).stream()
                .map(this::mapItemToResponseDto).toList();
        requestDto.getItems().addAll(items);
        return requestDto;
    }

    private void setItems(List<ItemRequestDto> requests) {
        List<Long> ids = requests.stream().map(ItemRequestDto::getId).toList();
        List<Item> items = itemStorage.findAllByRequestIdIn(ids);
        log.info("получил список всех items: {}", items);
        Map<Long, List<Item>> itemsMap = new HashMap<>();
        items.forEach(item -> {
            Long id = item.getRequest().getId();
            if (!itemsMap.containsKey(id)) {
                itemsMap.put(id, List.of(item));
            } else {
                List<Item> list = new ArrayList<>(itemsMap.get(id));
                list.add(item);
                itemsMap.put(id, list);
            }
        });
        log.info("получили Map {}", itemsMap);
        if (!itemsMap.isEmpty()) {
            requests.forEach(requestDto -> requestDto.getItems()
                    .addAll(itemsMap.get(requestDto.getId()).stream().map(this::mapItemToResponseDto).toList()));
        }
    }

    private ResponseDto mapItemToResponseDto(Item item) {
        return ResponseDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .ownerId(item.getId())
                .build();
    }
}
