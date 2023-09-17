package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createNewRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequests(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);
}
