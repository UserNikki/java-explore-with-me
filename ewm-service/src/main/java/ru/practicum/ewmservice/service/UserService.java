package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUser);

    List<UserDto> findUserById(List<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}
