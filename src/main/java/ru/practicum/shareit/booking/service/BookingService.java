package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataAccessException;
import ru.practicum.shareit.exceptions.ItemOwnerException;
import ru.practicum.shareit.exceptions.NotAvailableForBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDtoOutput create(BookingDtoInput bookingDtoInput, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
        Item item = itemRepository.findById(bookingDtoInput.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id =" + bookingDtoInput.getItemId()));
        if (item.getOwner().getId().equals(userId)) {
            throw new ItemOwnerException("Владелец предмета не может забронировать свой предмет.");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableForBooking("Предмет не доступен для бронирования.");
        }
        Booking booking = BookingMapper.mapBookingDtoInputToBooking(bookingDtoInput);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapBookingToBookingDtoOutput(bookingRepository.save(booking));
    }

    public BookingDtoOutput updateStatus(Long userId, Long bookingId, boolean isApproved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemOwnerException("Не найден пользователь с id =" + userId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id =" + bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ItemOwnerException("Изменять статус брони может только владелец предмета.");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapBookingToBookingDtoOutput(bookingRepository.save(booking));
    }

    public BookingDtoOutput getBooking(Long bookingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id =" + bookingId));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataAccessException("Информация о брони недоступна пользователю с id =" + userId);
        }
        return BookingMapper.mapBookingToBookingDtoOutput(booking);
    }

    public List<BookingDtoOutput> getBookingsForBooker(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        return switch (state) {
            case BookingState.ALL -> bookingRepository.findAllByBooker_IdOrderByStartAsc(userId)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.CURRENT -> bookingRepository.findAllBookerCurrentBookings(userId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.PAST -> bookingRepository.findAllBookerPastBookings(userId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.FUTURE -> bookingRepository.findAllBookerFutureBookings(userId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.WAITING -> bookingRepository.findAllByBooker_IdAndStatusOrderByStartAsc(userId,
                            BookingStatus.WAITING)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.REJECTED -> bookingRepository.findAllByBooker_IdAndStatusOrderByStartAsc(userId,
                            BookingStatus.REJECTED)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
        };
    }

    public List<BookingDtoOutput> getBookingsForOwner(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
        List<Long> itemIds = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(Item::getId)
                .toList();
        return switch (state) {
            case BookingState.ALL -> bookingRepository.findAllByItem_IdInOrderByStartAsc(itemIds)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.CURRENT -> bookingRepository.findAllOwnerCurrentBookings(itemIds, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.PAST -> bookingRepository.findAllOwnerPastBookings(itemIds, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.FUTURE -> bookingRepository.findAllOwnerFutureBookings(itemIds, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.WAITING -> bookingRepository.findAllByItem_IdInAndStatusOrderByStartAsc(itemIds,
                            BookingStatus.WAITING)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
            case BookingState.REJECTED -> bookingRepository.findAllByItem_IdInAndStatusOrderByStartAsc(itemIds,
                            BookingStatus.REJECTED)
                    .stream()
                    .map(BookingMapper::mapBookingToBookingDtoOutput)
                    .collect(Collectors.toList());
        };
    }
}