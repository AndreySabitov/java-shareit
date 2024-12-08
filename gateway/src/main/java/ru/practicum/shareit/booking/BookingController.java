package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBookingRequest(@Valid @RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.addBookingRequest(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getBookingsOfAllItemsOfOwner(userId, state);
    }
}
