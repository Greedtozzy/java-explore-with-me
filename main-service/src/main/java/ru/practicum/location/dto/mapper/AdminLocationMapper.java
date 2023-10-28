package ru.practicum.location.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.AdminLocationDto;
import ru.practicum.location.model.AdminLocation;

@UtilityClass
public class AdminLocationMapper {
    public static AdminLocationDto toDto(AdminLocation location) {
        return AdminLocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .rad(location.getRad())
                .name(location.getName())
                .build();
    }
}
