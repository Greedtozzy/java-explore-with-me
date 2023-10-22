package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.mapper.CompilationMapper;
import ru.practicum.compilations.dto.UpdateCompilationRequestDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.CompilationNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilationsPublic(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            log.info("Get all compilations");
            return repository.findAll(pageable).stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        }
        log.info("Get compilations pinned {}", pinned);
        return repository.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationByIdPublic(long compId) {
        log.info("Get compilation id {}", compId);
        return CompilationMapper.toDto(repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation with id=%d was not found", compId))));
    }

    @Override
    public CompilationDto add(NewCompilationDto dto) {
        log.info("Add compilation {}", dto);
        Compilation compilation = Compilation.builder()
                .events(dto.getEvents() == null ? new ArrayList<>() : eventRepository.findAllByIds(dto.getEvents()))
                .title(dto.getTitle())
                .pinned(dto.getPinned() == null ? false : dto.getPinned())
                .build();
        return CompilationMapper.toDto(repository.save(compilation));
    }

    @Override
    public void delete(long compId) {
        log.info("Delete compilation id {}", compId);
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto update(long compId, UpdateCompilationRequestDto dto) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        if (dto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIds(dto.getEvents()));
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        log.info("Update compilation id {} to {}", compId, dto);
        return CompilationMapper.toDto(repository.save(compilation));
    }
}
