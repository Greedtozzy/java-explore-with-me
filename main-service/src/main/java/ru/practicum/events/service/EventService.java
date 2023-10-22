package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.enums.SortEventsBy;
import ru.practicum.events.model.enums.State;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsByUser(long userId, int from, int size);

    EventFullDto addByUser(long userId, NewEventDto dto);

    EventFullDto getEventByIdByUser(long userId, long eventId, HttpServletRequest request);

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest dto);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories,
                                        Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable,
                                        SortEventsBy sort, int from, int size,
                                        HttpServletRequest request);

    EventFullDto getEventByIdPublic(long eventId, HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest dto);

    List<ParticipationRequestDto> getRequestsByEventIdByUser(long userId, long eventId);

    EventRequestStatusUpdateResult conformRequestsByUser(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);
}
