package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;
import ru.practicum.ewmservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserControllerAdmin {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("UserControllerAdmin POST {}", newUserRequest);
        return userService.create(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUserInfoByIds(@RequestParam(required = false) List<Long> ids,
                                          @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("UserControllerAdmin GET ids: {}, from: {}, size: {}", ids, from, size);
        return userService.findUserById(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(1) Long userId) {
        log.info("UserControllerAdmin DELETE by id: {}", userId);
        userService.delete(userId);
    }
}
