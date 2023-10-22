package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestByUserIdByUser(long userId);

    ParticipationRequestDto addRequestByUser(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
