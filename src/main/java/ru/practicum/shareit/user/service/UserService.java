package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    public UserDto updateUser(UserDto userDto, Long userId) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id=" + userId));
        if (userDto.getEmail() != null) {
            savedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            savedUser.setName(userDto.getName());
        }
        return UserMapper.mapToUserDto(userRepository.save(savedUser));
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id=" + userId)));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<UserDto> getAll() {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}