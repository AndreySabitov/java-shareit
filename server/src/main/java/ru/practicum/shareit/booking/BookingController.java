package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addBookingRequest(@RequestBody BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBookingRequest(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Boolean approved) {
        log.info("Сервер получил запрос на подтверждение бронирования");
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<ResponseBookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsOfItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsOfAllItemsOfOwner(userId, state);
    }
}
