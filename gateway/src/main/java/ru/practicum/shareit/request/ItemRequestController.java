package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestService;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody CreateRequestDto createRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("добавляем новый запрос");
        return itemRequestService.addRequest(createRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-id") Long userId) {
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOfOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsOfOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
