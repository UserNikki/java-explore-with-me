package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.mapper.CommentMapper;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CommentRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User id: '%d' not found", userId)));
        final Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event id: '%d' not found", eventId)));
        log.info("CommentServiceImpl create newCommentDto: {}", newCommentDto);
        final Comment comment = CommentMapper.toModel(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreatedDate(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, NewCommentDto newCommentDto) {
        isUserExists(userId);
        final Comment oldComment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Comment id: '%d' not found", commentId)));
        if (!oldComment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only owner can update comment");
        }
        log.info("CommentServiceImpl update newCommentDto: {}", newCommentDto.getText());
        oldComment.setText(newCommentDto.getText());
        return CommentMapper.toDto(commentRepository.save(oldComment));
    }

    @Override
    public List<CommentDto> getAllCommentsFromEvent(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event id: '%d' not found", eventId));
        }
        final Pageable pageable = createPageable(from, size, Sort.Direction.DESC, "createdDate");
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsFromUser(Long userId, Integer from, Integer size) {
        isUserExists(userId);
        final Pageable pageable = createPageable(from, size, Sort.Direction.DESC, "createdDate");
        return commentRepository.findAllByAuthorId(userId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByOwner(Long userId, Long commentId) {
        isUserExists(userId);
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteByIdAndAuthorId(commentId, userId);
        } else {
            throw new NotFoundException(String.format("Comment id: '%d' not found", commentId));
        }
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException(String.format("Comment id: '%d' not found", commentId));
        }
    }

    private void isUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id: '%d' does not exist", userId));
        }
    }
}
