package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.exceptions.ForbiddenArgumentException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategoryByAdmin(CategoryDto dto) {
        log.info("Add category {}", dto);
        return CategoryMapper.toDto(repository.save(CategoryMapper.toCategory(dto)));
    }

    @Override
    public CategoryDto updateCategoryByAdmin(long catId, CategoryDto dto) {
        Category category = CategoryMapper.toCategory(getCategoryById(catId));
        category.setName(dto.getName());
        log.info("Update category id {} to {}", catId, dto);
        return CategoryMapper.toDto(repository.save(category));
    }

    @Override
    public void deleteCategoryByAdmin(long catId) {
        repository.findById(catId)
                        .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id=%d was not found", catId)));
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new ForbiddenArgumentException("The category is not empty");
        }
        log.info("Delete category id {}", catId);
        repository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get all categories");
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        log.info("Get category id {}", catId);
        return CategoryMapper.toDto(repository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id=%d was not found", catId))));
    }
}
