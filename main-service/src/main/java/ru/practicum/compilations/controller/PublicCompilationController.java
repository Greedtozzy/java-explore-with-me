package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilationsPublic(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                      @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
                                                      @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Get compilations");
        return service.getCompilationsPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationByIdPublic(@PathVariable long compId) {
        log.info("Get comp by id {}", compId);
        return service.getCompilationByIdPublic(compId);
    }
}
