package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.enums.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.ForbiddenArgumentException;
import ru.practicum.exceptions.RequestNotFoundException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.enums.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getRequestByUserIdByUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d was not found", userId)));
        log.info("Get requests by user by id {}", userId);
        return repository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequestByUser(long userId, long eventId) {
        if (!repository.findAllByEventIdAndRequesterId(eventId, userId).isEmpty()) {
            throw new ForbiddenArgumentException(String.format("User %d already has participation request to event %d", userId, eventId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id=%d was not found", eventId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d was not found", userId)));
        if (userId == event.getInitiator().getId()) {
            throw new ForbiddenArgumentException(String.format("User %d is an initiator of event %d", userId, eventId));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ForbiddenArgumentException("Event must be published.");
        }
        if (event.getParticipantLimit() <= repository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) && event.getParticipantLimit() != 0) {
            throw new ForbiddenArgumentException(String.format("Event %d has maximum confirmed requests", eventId));
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        if (!event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        log.info("Add request from user id {} to event id {}", userId, eventId);
        return ParticipationRequestMapper.toDto(repository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request with id=%d was not found", requestId)));
        if (request.getRequester().getId() != userId) {
            throw new RuntimeException();
        }
        request.setStatus(RequestStatus.CANCELED);
        log.info("Request from user {} by id {} canceled", userId, requestId);
        return ParticipationRequestMapper.toDto(repository.save(request));
    }
}
