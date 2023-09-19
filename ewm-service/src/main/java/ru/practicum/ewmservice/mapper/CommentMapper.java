package ru.practicum.ewmservice.mapper;

import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.model.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.toShortDto(comment.getEvent()))
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .createdOn(comment.getCreatedDate())
                .text(comment.getText()).build();
    }

    public static Comment toModel(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText()).build();
    }
}
