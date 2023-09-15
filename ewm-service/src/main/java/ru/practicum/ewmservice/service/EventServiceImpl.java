package ru.practicum.ewmservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewmservice.dto.event.*;
import ru.practicum.ewmservice.dto.request.ParticipationRequestDto;
import ru.practicum.ewmservice.enums.EventStateEnum;
import ru.practicum.ewmservice.enums.StateActionEnum;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.mapper.EventMapper;
import ru.practicum.ewmservice.mapper.RequestMapper;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.Request;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.ewmservice.enums.RequestStatusEnum.*;
import static ru.practicum.ewmservice.util.Const.TIME_FORMATTER;
import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;


    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        isExistsUser(userId);
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        final List<Event> result = eventRepository.findAllByInitiatorId(userId, pageable);
        return result.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id: " + eventId + " not found."));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new IllegalStateException("Limit trouble in EventServiceImpl updateEventRequestStatus");
        }
        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId,
                statusUpdateRequest.getRequestIds());
        boolean isStatusPending = requests.stream()
                .anyMatch(request -> !request.getStatus().equals(PENDING));
        if (isStatusPending) {
            throw new IllegalStateException("Request status cannot be changed'");
        }
        switch (statusUpdateRequest.getStatus()) {
            case CONFIRMED:
                return createConfirmedStatus(requests, event);
            case REJECTED:
                return createRejectedStatus(requests, event);
            default:
                throw new IllegalStateException("Status problem in EventServiceImpl updateStatusRequest");
        }
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id = '" + userId + "' not found!"));
        isBeforeTwoHours(LocalDateTime.parse(eventDto.getEventDate(), TIME_FORMATTER));
        final Category category = getCategoryById(eventDto.getCategory());
        final Event event = EventMapper.toModel(eventDto);
        log.info("EventServiceImpl createNewEvent userId: {} eventDto: {}", userId, eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(EventStateEnum.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0);
        event.setViews(0);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        isExistsUser(userId);
        final Event event = getEvenByInitiatorAndEventId(userId, eventId);
        log.info("EventServiceImpl getEventByUserIdAndEventId {}", event);
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest update) {
        isExistsUser(userId);
        final Event oldEvent = getEvenByInitiatorAndEventId(userId, eventId);
        log.info("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" +
                "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" +
                "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" +
                "BBBBOOOOODYYYYYYYYY: {}", update);
        log.info("OLDEVEEEEEEEEEEEEEEEENT {}", oldEvent);
        if (oldEvent.getState().equals(EventStateEnum.PUBLISHED)) {
            throw new IllegalArgumentException("ONLY PENDING Status can be updated: problem in EventServiceImpl " +
                    "updateEventByUsersIdAndEventIdFromUser");
        }
        if (update.getEventDate() != null) {
            final LocalDateTime newDate = LocalDateTime.parse(update.getEventDate(), TIME_FORMATTER);
            isBeforeTwoHours(newDate);
            oldEvent.setEventDate(newDate);
        }
        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Only owner can update event");
        }
        if (update.getAnnotation() != null && !update.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(update.getAnnotation());
        }
        if (update.getCategory() != null) {
            final Category category = getCategoryById(update.getCategory());
            oldEvent.setCategory(category);
        }
        if (update.getDescription() != null) {
            oldEvent.setDescription(update.getDescription());
        }
        //забыл про пейд
        if (update.getPaid() != null) {
            oldEvent.setPaid(update.getPaid());
        }
        if (update.getLocation() != null) {
            oldEvent.setLocation(update.getLocation());
        }
        if (update.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(update.getParticipantLimit());
        }
        if (update.getRequestModeration() != null) {
            oldEvent.setRequestModeration(update.getRequestModeration());
        }
        if (update.getStateAction() != null) {
            switch (update.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setState(EventStateEnum.PENDING);
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setState(EventStateEnum.CANCELED);
                    break;
            }
        }
        if (update.getTitle() != null) {
            oldEvent.setTitle(update.getTitle());
        }
        Event eventUpdated = eventRepository.save(oldEvent);
        log.info("Event updated: {}", eventUpdated);
        return EventMapper.toFullDto(eventUpdated);
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId) {
        isExistsUser(userId);
        if (!eventRepository.existsByInitiatorIdAndId(userId, eventId)) {
            throw new IllegalArgumentException("User is not the owner");
        }
        final List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }


    @Override
    public List<EventFullDto> getAllEventForParamFromAdmin(List<Long> users,
                                                           List<String> states,
                                                           List<Long> categories,
                                                           LocalDateTime rangeStart,
                                                           LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Start is after end. it's not cool bro");
        }
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        List<Event> events = eventRepository.findAll(specification, pageable);
        return events.stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByEventIdFromAdmin(Long eventId, UpdateEventAdminRequest update) {
        final Event oldEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id = '" + eventId + "' not found"));

        if (oldEvent.getState().equals(EventStateEnum.PUBLISHED) || oldEvent.getState().equals(EventStateEnum.CANCELED)) {
            throw new IllegalArgumentException("Cannot update because of status");
        }
        if (update.getAnnotation() != null && !update.getAnnotation().isBlank()) {
            if (update.getAnnotation().length() < 20 || update.getAnnotation().length() > 2000) {
                throw new IllegalArgumentException("incorrect length of the annotation parameter");
            } else {
                oldEvent.setAnnotation(update.getAnnotation());
            }
        }
        if (update.getCategory() != null) {
            final Category category = getCategoryById(update.getCategory());
            oldEvent.setCategory(category);
        }
        if (update.getDescription() != null && !update.getDescription().isEmpty()) {
            oldEvent.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            isBeforeTwoHours(LocalDateTime.parse(update.getEventDate(), TIME_FORMATTER));
            oldEvent.setEventDate(LocalDateTime.parse(update.getEventDate(), TIME_FORMATTER));
        }
        if (update.getLocation() != null) {
            oldEvent.setLocation(update.getLocation());
        }
        if (update.getPaid() != null) {
            oldEvent.setPaid(update.getPaid());
        }
        if (update.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(update.getParticipantLimit());
        }
        if (update.getRequestModeration() != null) {
            oldEvent.setRequestModeration(update.getRequestModeration());
        }
        if (update.getStateAction() != null) {
            if (update.getStateAction().equals(StateActionEnum.PUBLISH_EVENT)) {
                oldEvent.setState(EventStateEnum.PUBLISHED);
            } else if (update.getStateAction().equals(StateActionEnum.REJECT_EVENT)) {
                oldEvent.setState(EventStateEnum.CANCELED);
            }
        }
        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            oldEvent.setTitle(update.getTitle());
        }
        Event eventAfterUpdate = eventRepository.save(oldEvent);

        return EventMapper.toFullDto(eventAfterUpdate);
    }

    @Override
    public List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new IllegalArgumentException("End time before start time");
            }
        }
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        addStatistic(request);
        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventStateEnum.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable);
        setViewsOfEvents(resultEvents);

        return resultEvents.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        final Event event = eventRepository.findByIdAndState(eventId, EventStateEnum.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Event with id: '" + eventId + "' not found"));
        addStatistic(request);
        setViewsOfEvents(List.of(event));
        return EventMapper.toFullDto(event);
    }

    private void isBeforeTwoHours(LocalDateTime startDate) {
        if (startDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Event cannot start less than two hours from now");
        }
    }

    private void isExistsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id: '" + userId + "' not found!");
        }
    }

    private Event getEvenByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new NotFoundException("Event with id = '" + eventId + "' and/or initiator with id = '" + userId + "' not found"));
    }

    private Category getCategoryById(Long caId) {
        return categoryRepository.findById(caId).orElseThrow(
                () -> new NotFoundException("Category with id = '" + caId + "' not found"));
    }

    private Request getRequestOrThrow(Long eventId, Long reqId) {
        return requestRepository.findByEventIdAndId(eventId, reqId).orElseThrow(
                () -> new NotFoundException("Request id: '" + reqId + "' not found, event id: '" + eventId + "'"));
    }

    private void addStatistic(HttpServletRequest request) {
        String app = "ewm-service";
        statsClient.saveStats(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void setViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        ResponseEntity<Object> response = statsClient.getStats("2000-01-01 00:00:00", "2100-01-01 00:00:00", uris, false);
        final ObjectMapper mapper = new ObjectMapper();
        final List<ViewStatsDto> viewStatsList = mapper.convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });
        for (Event event : events) {
            ViewStatsDto currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views.intValue() + 1);
        }
        eventRepository.saveAll(events);
    }

    private EventRequestStatusUpdateResult createConfirmedStatus(List<Request> requests, Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new IllegalStateException("Participant limit is over");
        }
        int freePlaces = event.getParticipantLimit() - event.getConfirmedRequests();
        List<Request> confirmedRequests;
        List<Request> rejectedRequests;
        if (requests.size() <= freePlaces) {
            confirmedRequests = requests.stream()
                    .peek(request -> request.setStatus(CONFIRMED))
                    .collect(Collectors.toList());
            rejectedRequests = List.of();
        } else {
            confirmedRequests = requests.stream()
                    .limit(freePlaces)
                    .peek(request -> request.setStatus(CONFIRMED))
                    .collect(Collectors.toList());
            rejectedRequests = requests.stream()
                    .skip(freePlaces)
                    .peek(request -> request.setStatus(REJECTED))
                    .collect(Collectors.toList());
        }
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
        eventRepository.save(event);
        List<Request> updatedRequests = Stream.concat(confirmedRequests.stream(), rejectedRequests.stream())
                .collect(Collectors.toList());
        requestRepository.saveAll(updatedRequests);
        List<ParticipationRequestDto> confirmedRequestsDto = confirmedRequests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequestsDto = rejectedRequests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }

    private EventRequestStatusUpdateResult createRejectedStatus(List<Request> requests, Event event) {
        requests.forEach(request -> request.setStatus(REJECTED));
        requestRepository.saveAll(requests);
        List<ParticipationRequestDto> rejectedRequests = requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(Collections.emptyList(), rejectedRequests);
    }
}
