package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.mapper.UserMapper;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUser) {
        if (userRepository.existByName(newUser.getName()))
            throw new ValidationException("User name is not unique");
        log.info("UserServiceImpl create user: {}", newUser);
        final User user = UserMapper.toModel(newUser);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findUserById(List<Long> ids, Integer from, Integer size) {
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        log.info("UserServiceImpl findUserById ids: {} from: {} size {}", ids, from, size);
        List<User> result = (ids != null) ?
                userRepository.findByIdIn(ids, pageable) :
                userRepository.findAll(pageable).getContent();
        return result.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.info("UserServiceImpl delete id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id '" + userId + "' not found");
        }
        userRepository.deleteById(userId);
    }
}
