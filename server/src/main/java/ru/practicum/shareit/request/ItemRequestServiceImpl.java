package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ResponseMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage requestStorage;
    private final UserStorage userStorage;
    private final ResponseToRequestStorage responseStorage;

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
        requests.forEach(this::setResponses);
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
        setResponses(requestDto);
        return requestDto;
    }

    private void setResponses(ItemRequestDto requestDto) {
        Set<ResponseDto> responses = responseStorage.findAllByItemRequestId(requestDto.getId()).stream()
                .map(ResponseMapper::mapToDto).collect(Collectors.toSet());
        log.info("получили список ответов {}", responses);
        requestDto.getItems().addAll(responses);
    }
}
