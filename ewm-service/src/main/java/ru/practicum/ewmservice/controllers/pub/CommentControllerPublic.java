package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentFromEvent(@PathVariable(value = "eventId") Long eventId,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return commentService.getAllCommentsFromEvent(eventId, from, size);
    }
}
