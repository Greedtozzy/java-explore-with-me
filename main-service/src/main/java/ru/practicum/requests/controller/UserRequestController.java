package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class UserRequestController {
    private final RequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getByUserId(@PathVariable(name = "userId") long userId) {
        return service.getRequestByUserIdByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable(name = "userId") long userId,
                                       @RequestParam(value = "eventId") long eventId) {
        return service.addRequestByUser(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable(name = "userId") long userId,
                       @PathVariable(name = "requestId") long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}