package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addBookingRequest(@Valid @RequestBody BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.addBookingRequest(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@PathVariable BigInteger bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@PathVariable BigInteger bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<ResponseBookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsOfItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsOfAllItemsOfOwner(userId, state);
    }
}
