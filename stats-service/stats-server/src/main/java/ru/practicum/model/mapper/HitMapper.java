package ru.practicum.model.mapper;

import ru.practicum.HitDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    public static Hit toHit(HitDto dto) {
        return Hit.builder()
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
