package ru.practicum.model.mapper;

import ru.practicum.HitDto;
import ru.practicum.model.Hit;

public class HitMapper {
    public static Hit toHit(HitDto dto) {
        return Hit.builder()
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
