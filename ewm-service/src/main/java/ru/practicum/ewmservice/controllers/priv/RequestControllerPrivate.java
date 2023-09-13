package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestControllerPrivate {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createNewRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                    @Valid @RequestParam(name = "eventId") Long eventId) {
        log.info("RequestControllerPrivate POST createNewRequest user id: {}, event id: {}", userId, eventId);
        return requestService.createNewRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.info("RequestControllerPrivate GET getAllRequests user id: {}", userId);
        return requestService.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                 @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.info("RequestControllerPrivate GET cancelRequest user id: {}, request id: {}", userId, requestId);
        return requestService.cancel(userId, requestId);
    }
}
