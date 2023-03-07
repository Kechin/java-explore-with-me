package ru.practicum.category;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long id);

    @Transactional
    CategoryDto create(NewCategoryDto categoryDto);

    @Transactional
    CategoryDto update(CategoryDto categoryDto, Long catId);

    @Transactional
    void delete(Long catId);
}
