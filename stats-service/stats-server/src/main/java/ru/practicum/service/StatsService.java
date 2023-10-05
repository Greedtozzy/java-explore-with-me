package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatDto;

import java.util.List;

public interface StatsService {
    void hit(HitDto dto);

    List<StatDto> get(String start, String end, String[] uris, boolean unique);
}
