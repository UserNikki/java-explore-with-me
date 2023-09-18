package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, NewCommentDto input);

    CommentDto update(Long userId, Long commentId, NewCommentDto update);

    List<CommentDto> getAllCommentsFromEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getAllCommentsFromUser(Long userId, Integer from, Integer size);

    void deleteCommentByOwner(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);
}
