package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.AdminLocationDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {
    private final LocationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminLocationDto addLocation(@Valid @RequestBody NewLocationDto dto) {
        return service.addLocation(dto);
    }

    @GetMapping
    public List<AdminLocationDto> get(@RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                      @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        return service.get(from, size);
    }

    @GetMapping("/{locId}")
    public AdminLocationDto getById(@PathVariable long locId) {
        return service.getById(locId);
    }

    @DeleteMapping("/{locId}")
    public void delete(@PathVariable long locId) {
        service.delete(locId);
    }
}
