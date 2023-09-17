package ru.practicum.ewmservice.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.event.*;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventControllerPrivate {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable(value = "userId") @Min(1) Long userId,
                                                    @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("EventControllerPrivate getAllEventsByUserId userId: {} from: {} size: {}", userId, from, size);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@PathVariable(value = "userId") @Min(1) Long userId,
                                       @Valid @RequestBody NewEventDto newEventDto) {
        log.info("EventControllerPrivate POST createNewEvent newEventDto: {}", newEventDto);
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getFullEventByOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                            @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info("EventControllerPrivate GET getFullEventByOwner user id: {}, event id: {}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByOwner(@PathVariable(value = "userId") @Min(0) Long userId,
                                           @PathVariable(value = "eventId") @Min(0) Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        log.info("EventControllerPrivate PATCH updateEventByOwner updateRequest: {}", updateRequest);
        log.info("EventControllerPrivate PATCH updateEventByOwner user id: {}, event id: {}", userId, eventId);
        return eventService.updateEventByUsersIdAndEventIdFromUser(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info("EventControllerPrivate getAllRequestByEventFromOwner GET");
        log.info("user id: {}, event id: {}", userId, eventId);
        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("EventControllerPrivate PATCH updateStatusRequestFromOwner updateRequest {}", updateRequest);
        log.info("user id: {}, event id: {}", userId, eventId);
        return eventService.updateStatusRequest(userId, eventId, updateRequest);
    }
}
