package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class UserEventController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@Positive @PathVariable long userId,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return service.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addByUser(@Positive @PathVariable long userId,
                                  @Valid @RequestBody NewEventDto event) {
        return service.addByUser(userId, event);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdByUser(@Positive @PathVariable long userId,
                                           @Positive @PathVariable long eventId,
                                           HttpServletRequest request) {
        return service.getEventByIdByUser(userId, eventId, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@Positive @PathVariable long userId,
                                          @Positive @PathVariable long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest event) {
        return service.updateEventByUser(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEventIdByUser(@Positive @PathVariable long userId,
                                                                    @Positive @PathVariable long eventId) {
        return service.getRequestsByEventIdByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult conformRequestsByUser(@Positive @PathVariable long userId,
                                                                @Positive @PathVariable long eventId,
                                                                @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return service.conformRequestsByUser(userId, eventId, updateRequest);
    }
}
