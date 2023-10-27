package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatDto;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.dto.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.enums.AdminStateAction;
import ru.practicum.events.model.enums.SortEventsBy;
import ru.practicum.events.model.enums.State;
import ru.practicum.events.model.enums.UserStateAction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.AdminLocation;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.AdminLocationRepository;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.enums.NewRequestStatus;
import ru.practicum.requests.model.enums.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final AdminLocationRepository adminLocationRepository;
    @Autowired
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEventsByUser(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d was not found", userId)));
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get events by user id {}", userId);
        return repository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addByUser(long userId, NewEventDto dto) {
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenArgumentException(String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s", dto.getEventDate()));
        }
        Event event = Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .title(dto.getTitle())
                .initiator(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d was not found", userId))))
                .category(categoryRepository.findById(dto.getCategory())
                        .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id=%d was not found", dto.getCategory()))))
                .location(locationRepository.findByLonAndLat(dto.getLocation().getLon(), dto.getLocation().getLat())
                        .orElse(addLocation(dto.getLocation())))
                .created(LocalDateTime.now())
                .eventDate(dto.getEventDate())
                .views(0)
                .state(State.PENDING)
                .build();
        if (dto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        log.info("Add event {} by user id {}", dto, userId);
        return EventMapper.toFullDto(repository.save(event));
    }

    @Override
    public EventFullDto getEventByIdByUser(long userId, long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id=%d was not found", eventId)));
        log.info("Get event id {} by user id {}", eventId, userId);
        return EventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest dto) {
        Event event = validation(userId, eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenArgumentException("Only pending or canceled events can be changed");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenArgumentException(String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s", event.getEventDate()));
        }

        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationRequestException(String.format("EventDate must not be before 2 hours from current time. Value: %s", dto.getEventDate()));
            }
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id=%d was not found", dto.getCategory()))));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationRepository.findByLonAndLat(dto.getLocation().getLon(), dto.getLocation().getLat())
                    .orElse(addLocation(dto.getLocation())));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == UserStateAction.SEND_TO_REVIEW) event.setState(State.PENDING);
            if (dto.getStateAction() == UserStateAction.CANCEL_REVIEW) event.setState(State.CANCELED);
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        log.info("Event id {} updated {}", eventId, dto);
        return EventMapper.toFullDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, SortEventsBy sort, int from, int size,
                                               HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        dateValidator(rangeStart, rangeEnd);
        List<Specification<Event>> specifications = new ArrayList<>();
        if (categories != null) {
            List<Category> categoryList = categories.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(categoryIdIn(categoryList));
        }
        specifications.add(paid == null ? null : paidIs(paid));
        specifications.add(eventDateGreaterOrEquals(rangeStart));
        specifications.add(rangeEnd == null ? null : eventDateLess(rangeEnd));
        specifications.add(stateIn(List.of(State.PUBLISHED)));
        Specification<Event> specification = specifications.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElseThrow();
        List<Event> events;
        if (text == null) {
            events = repository.findAll(specification, pageable).stream().collect(Collectors.toList());
        } else {
            events = new ArrayList<>(repository.findAllByText(text, specification, pageable));
        }
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews));
                    break;
                default:
                    throw new ValidationRequestException("Sort is not valid");
            }
        }
        if (onlyAvailable) {
            log.info("Get events with text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}, sort by {}",
                    text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
            addHit(request);
            return events.stream()
                    .filter(event -> (event.getParticipantLimit() > event.getConfirmedRequests().size()) || event.getParticipantLimit() == 0)
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList());
        }
        log.info("Get events with text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}, sort by {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        addHit(request);
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new EventNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        Integer views = viewsCount(eventId);
        addHit(request);
        Integer updatedViews = viewsCount(eventId);
        if (views < updatedViews) {
            event.setViews(event.getViews() + 1);
        }
        log.info("Get event id {}", eventId);
        return EventMapper.toFullDto(repository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsInLocationPublic(long locId, boolean onlyAvailable) {
        if (onlyAvailable) {
            log.info("Get only available events in location id={}.", locId);
            return repository.findAll().stream()
                    .filter(event -> isEventInLocation(event.getLocation(), locId))
                    .filter(event -> (event.getParticipantLimit() > event.getConfirmedRequests().size()) || event.getParticipantLimit() == 0)
                    .map(EventMapper::toFullDto)
                    .collect(Collectors.toList());
        }
        log.info("Get all events in location id={}.", locId);
        return repository.findAll().stream()
                .filter(event -> isEventInLocation(event.getLocation(), locId))
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               int from, int size) {
        dateValidator(rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Specification<Event>> specifications = new ArrayList<>();
        if (users != null) {
            List<User> userList = users.stream()
                    .map(id -> userRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(initiatorIdIn(userList));
        }
        if (categories != null) {
            List<Category> categoryList = categories.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(categoryIdIn(categoryList));
        }
        specifications.add(states == null ? null : stateIn(states));
        specifications.add(rangeStart == null ? null : eventDateGreaterOrEquals(rangeStart));
        specifications.add(rangeEnd == null ? null : eventDateLess(rangeEnd));
        Specification<Event> specification = specifications.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
        List<Event> events;
        if (specification != null) {
            events = repository.findAll(specification, pageable).toList();
        } else {
            events = repository.findAll(pageable).toList();
        }
        log.info("Get events by admin with users {}, states {}, categories {}, rangeStart {}, rangeEnd {}",
                users, states, categories, rangeStart, rangeEnd);
        return events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest dto) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(""));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenArgumentException(String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s", event.getEventDate()));
        }

        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ForbiddenArgumentException(String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s", dto.getEventDate()));
            }
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id=%d was not found", dto.getCategory()))));
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationRepository.findByLonAndLat(dto.getLocation().getLon(), dto.getLocation().getLat())
                    .orElse(addLocation(dto.getLocation())));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                if (event.getState() != State.PENDING) {
                    throw new ForbiddenArgumentException(String.format("Cannot publish the event because it's not in the right state: %s", event.getState()));
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (event.getState() == State.PUBLISHED) {
                    throw new ForbiddenArgumentException(String.format("Cannot publish the event because it's not in the right state: %s", event.getState()));
                }
                event.setState(State.CANCELED);
            }
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        log.info("Update event {} by admin to {}", eventId, dto);
        return EventMapper.toFullDto(repository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventIdByUser(long userId, long eventId) {
        validation(userId, eventId);
        log.info("Get requests by event {} by user {}", eventId, userId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult conformRequestsByUser(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = validation(userId, eventId);
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ForbiddenArgumentException("Do not need accept requests, cause participant limit equals 0 or pre-moderation off");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        int limit = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (limit == event.getParticipantLimit()) {
            throw new ForbiddenArgumentException("The participant limit has been reached");
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ForbiddenArgumentException("Request must have status PENDING");
            }
            if (updateRequest.getStatus() == NewRequestStatus.CONFIRMED) {
                if (limit < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(ParticipationRequestMapper.toDto(request));
                    limit++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(ParticipationRequestMapper.toDto(request));
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.toDto(request));
            }
        }
        requestRepository.saveAll(requests);
        log.info("Requests statuses are changed");
        return result;
    }

    private Event validation(long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d was not found", userId)));
        return repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private Integer viewsCount(long eventId) {
        ResponseEntity<StatDto[]> response = statsClient.get(LocalDateTime.now().minusYears(1),
                LocalDateTime.now(),
                new String[]{"/events/" + eventId},
                true);
        int views = 0;
        Optional<StatDto> stat;
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            stat = Arrays.stream(response.getBody()).findFirst();
            if (stat.isPresent()) {
                views = Math.toIntExact(stat.get().getHits());
            }
        }
        return views;
    }

    private void addHit(HttpServletRequest request) {
        statsClient.hit(HitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Location addLocation(LocationDto dto) {
        return locationRepository.save(Location.builder()
                .lon(dto.getLon())
                .lat(dto.getLat())
                .build());
    }

    private void dateValidator(LocalDateTime start, LocalDateTime end) {
        if (end != null && start.isAfter(end)) {
            throw new ValidationRequestException("Start must be before end");
        }
    }

    private Specification<Event> initiatorIdIn(List<User> users) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator")).value(users);
    }

    private Specification<Event> stateIn(List<State> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states);
    }

    private Specification<Event> categoryIdIn(List<Category> categories) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category")).value(categories);
    }

    private Specification<Event> eventDateGreaterOrEquals(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    private Specification<Event> eventDateLess(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd);
    }

    private Specification<Event> paidIs(Boolean paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private boolean isEventInLocation(Location eventLoc, long adminLocId) {
        AdminLocation adminLoc = adminLocationRepository.findById(adminLocId)
                .orElseThrow(() -> new LocationNotFoundException(String.format("Location by id %d not found", adminLocId)));
        double range;
        if (eventLoc.getLat() == adminLoc.getLat() && eventLoc.getLon() == adminLoc.getLon()) {
            range = 0;
        } else {
            double avgLat = (eventLoc.getLat() + adminLoc.getLat()) / 2;
            double kmInDegree = Math.cos(avgLat) * 111.3;
            range = Math.abs(Math.sqrt(Math.pow((eventLoc.getLat() - adminLoc.getLat()), 2) +
                    Math.pow((eventLoc.getLon() - adminLoc.getLon()), 2)) * kmInDegree);
        }
        return range <= adminLoc.getRad();
    }
}