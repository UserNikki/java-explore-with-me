package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerPrivate {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createNewComment(@PathVariable(value = "userId") Long userId,
                                       @Valid @RequestBody NewCommentDto newCommentDto,
                                       @RequestParam(value = "eventId") Long eventId) {
        return commentService.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody NewCommentDto update) {
        return commentService.update(userId, commentId, update);
    }

    @GetMapping
    public List<CommentDto> getAllCommentByUser(@PathVariable(value = "userId") Long userId,
                                                @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return commentService.getAllCommentsFromUser(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "userId") Long userId,
                              @PathVariable(value = "commentId") Long commentId) {

        commentService.deleteCommentByOwner(userId, commentId);
    }
}
