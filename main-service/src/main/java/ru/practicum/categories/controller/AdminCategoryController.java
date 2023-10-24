package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategoryByAdmin(@Valid @RequestBody CategoryDto category) {
        return service.addCategoryByAdmin(category);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryByAdmin(@PathVariable(name = "catId") long catId,
                                             @Valid @RequestBody CategoryDto category) {
        return service.updateCategoryByAdmin(catId, category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryByAdmin(@PathVariable(name = "catId") long catId) {
        service.deleteCategoryByAdmin(catId);
    }
}
