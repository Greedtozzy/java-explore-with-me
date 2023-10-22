package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.enums.SortEventsBy;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@RequestParam(value = "text", required = false) String text,
                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                               @RequestParam(value = "paid", required = false) Boolean paid,
                                               @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) boolean onlyAvailable,
                                               @RequestParam(value = "sort", required = false) SortEventsBy sort,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                               HttpServletRequest request) {
        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdPublic(@PathVariable long eventId,
                                           HttpServletRequest request) {
        return service.getEventByIdPublic(eventId, request);
    }
}
