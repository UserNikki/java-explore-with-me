package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;
import ru.practicum.ewmservice.dto.user.UserShortDto;
import ru.practicum.ewmservice.model.User;

@UtilityClass
public class UserMapper {

    public static User toModel(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName()).build();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName()).build();
    }

    public static User toModel(NewUserRequest newUserDto) {
        return User.builder()
                .email(newUserDto.getEmail())
                .name(newUserDto.getName()).build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
