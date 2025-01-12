package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST с телом запроса {}", userDto);
        UserDto response = userService.createUser(userDto);
        log.info("Получен ответ POST с телом ответа {}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        UserDto response = userService.getUserById(userId);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос PATCH для пользователь с id = {} с телом запроса {}", userId, userDto);
        UserDto response = userService.updateUser(userDto, userId);
        log.info("Получен ответ PATCH с телом ответа {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        List<UserDto> response = userService.getAll();
        log.info("Получен ответ GET с телом запроса {}", response);
        return response;
    }
}