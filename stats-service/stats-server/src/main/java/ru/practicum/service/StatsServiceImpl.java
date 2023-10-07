package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;
import ru.practicum.model.mapper.HitMapper;
import ru.practicum.model.mapper.StatMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public void hit(HitDto dto) {
        repository.save(HitMapper.toHit(dto));
    }

    @Override
    public List<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Stat> stats;
        if (uris == null) {
            if (unique) {
                stats = repository.findAllUniqueIp(start, end);
            } else {
                stats = repository.findAllNotUniqueIp(start, end);
            }
        } else {
            if (unique) {
                stats = repository.findUniqueIpByUris(start, end, uris);
            } else {
                stats = repository.findNotUniqueIpByUris(start, end, uris);
            }
        }
        return stats.stream()
                .map(StatMapper::toDto)
                .collect(Collectors.toList());
    }
}
