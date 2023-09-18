package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerAdmin {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
