package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.event.EventFullDto;
import ru.practicum.ewmservice.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findAllByParams(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<String> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("EventControllerAdmin GET findAllByParams");
        log.info("Request params: users: {}, events: {}, categories: {}, range start: {}, " +
                "range end: {}, from: {}, size: {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAllEventForParamFromAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @Min(1) Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateRequest) {
        log.info("EventControllerAdmin PATCH updateEventByAdmin");
        log.info("Event id: {}, updateRequest: {}", eventId, updateRequest);
        return eventService.updateEventByEventIdFromAdmin(eventId, updateRequest);
    }
}
