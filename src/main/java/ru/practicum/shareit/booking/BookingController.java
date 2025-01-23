package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody BookingDtoInput bookingDtoInput) {
        log.info("Получен запрос POST с телом запроса {}", bookingDtoInput);
        BookingDtoOutput response = bookingService.create(bookingDtoInput, userId);
        log.info("Получен ответ POST с телом ответа {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(name = "approved") boolean isApproved) {
        log.info("Получен запрос PATCH для обновления статуса брони с id {}", bookingId);
        BookingDtoOutput response = bookingService.updateStatus(userId, bookingId, isApproved);
        log.info("Получен ответ PATCH с телом ответа {}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Поулчен запрос GET с бронью id {}", bookingId);
        BookingDtoOutput response = bookingService.getBooking(bookingId, userId);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }

    @GetMapping
    public List<BookingDtoOutput> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Получен запрос GET для всех броней для пользователя с id = " + userId);
        BookingState stateEnum;
        try {
            stateEnum = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неверное значение параметра статуса");
        }
        List<BookingDtoOutput> response = bookingService.getBookingsForBooker(userId,
                stateEnum);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Получен запрос GET на получение всех броней для владельца с id = " + userId);
        BookingState stateEnum;
        try {
            stateEnum = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неверное значение параметра статуса");
        }
        List<BookingDtoOutput> response = bookingService.getBookingsForOwner(userId,
                stateEnum);
        log.info("Поулчен ответ GET с телом ответа{}", response);
        return response;
    }
}