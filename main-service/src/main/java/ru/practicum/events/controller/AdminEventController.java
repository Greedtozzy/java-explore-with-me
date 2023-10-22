package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.model.enums.State;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(value = "users", required = false) List<Long> users,
                                               @RequestParam(value = "states", required = false) List<State> states,
                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                               @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return service.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest event) {
        return service.updateEventByAdmin(eventId, event);
    }
}
