package ru.practicum.location.service;

import ru.practicum.location.dto.AdminLocationDto;
import ru.practicum.location.dto.NewLocationDto;

import java.util.List;

public interface LocationService {
    AdminLocationDto addLocation(NewLocationDto dto);

    List<AdminLocationDto> get(int from, int size);

    AdminLocationDto getById(long locId);

    void delete(long locId);
}
