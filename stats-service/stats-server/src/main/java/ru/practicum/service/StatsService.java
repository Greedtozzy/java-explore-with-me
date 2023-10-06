package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void hit(HitDto dto);

    List<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
