package ru.practicum.model.mapper;

import ru.practicum.StatDto;
import ru.practicum.model.Stat;

public class StatMapper {
    public static StatDto toDto(Stat stat) {
        return StatDto.builder()
                .hits(stat.getHits())
                .app(stat.getApp())
                .uri(stat.getUri())
                .build();
    }
}
