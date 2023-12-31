package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.enums.EventState;
import ru.practicum.ewmservice.enums.RequestStatus;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.mapper.RequestMapper;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.Request;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createNewRequest(Long userId, Long eventId) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id = '" + userId + "' not found"));
        final Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id '" + eventId + "' not found"));
        final LocalDateTime createdOn = LocalDateTime.now();
        validationNewRequest(event, userId, eventId);
        final Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        int countRequestConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getConfirmedRequests() != countRequestConfirmed) {
            event.setConfirmedRequests(countRequestConfirmed);
            eventRepository.save(event);
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllRequests(Long userId) {
        isUserExists(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        isUserExists(userId);
        final Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException(String
                        .format("Request with id: '%d', with same requester id = '%d' not found", requestId, userId)));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new IllegalArgumentException("Request already canceled or rejected");
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    private void validationNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalStateException("Owner cannot be a participant");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository
                .countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new IllegalStateException("Participant Limit is full");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalStateException("Event not published");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new IllegalStateException("Cannot add duplicate request");
        }
    }

    private void isUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id: '%d' not found", userId));
        }
    }
}
