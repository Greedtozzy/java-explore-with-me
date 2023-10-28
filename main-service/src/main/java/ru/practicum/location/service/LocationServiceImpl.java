package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.LocationNotFoundException;
import ru.practicum.location.dto.AdminLocationDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.mapper.AdminLocationMapper;
import ru.practicum.location.model.AdminLocation;
import ru.practicum.location.repository.AdminLocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final AdminLocationRepository repository;

    @Override
    public AdminLocationDto addLocation(NewLocationDto dto) {
        AdminLocation location = AdminLocation.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .rad(dto.getRad())
                .name(dto.getName())
                .build();
        log.info("Create location {}", dto);
        return AdminLocationMapper.toDto(repository.save(location));
    }

    @Override
    public List<AdminLocationDto> get(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("");
        return repository.findAll(pageable).stream()
                .map(AdminLocationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminLocationDto getById(long locId) {
        log.info("");
        return AdminLocationMapper.toDto(repository.findById(locId)
                .orElseThrow(() -> new LocationNotFoundException(String.format("Location by id %d not found", locId))));
    }

    @Override
    public void delete(long locId) {
        log.info("");
        repository.deleteById(locId);
    }
}