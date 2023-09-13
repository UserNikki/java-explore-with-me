package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.event.*;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventFullDto> getAllEventForParamFromAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest update);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId);

    //EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest update);

    EventFullDto updateEventByEventIdFromAdmin(Long eventId, UpdateEventAdminRequest update);

    List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
