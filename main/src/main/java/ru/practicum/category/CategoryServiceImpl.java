package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");

    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        return CategoryMapper.toCategoryDtos(categoryRepository.findAll(pageable).toList());
    }

    public CategoryDto getById(Long id) {
        return CategoryMapper.toCategoryDto(getCategory(id));
    }

    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        String name = categoryDto.getName();
        if (categoryRepository.findFirstByNameIs(name) == null) {
            log.info(" Добавление новой категории {}", categoryRepository.findFirstByNameIs(name));
            Category category = new Category(name);
            return CategoryMapper.toCategoryDto(categoryRepository.save(category));
        }
        throw new ConflictException("Данное имя уже существует");

    }

    @Transactional
    public CategoryDto update(CategoryDto categoryDto, Long catId) {

        Category category = getCategory(catId);
        String newName = categoryDto.getName();
        if (categoryRepository.findFirstByNameIs(newName) == null) {
            log.info("Старое имя: {} - Новое имя: {}", category.getName(), newName);
            category.setName(newName);
            return CategoryMapper.toCategoryDto(category);
        }
        throw new ConflictException("Данное имя уже существует");
    }

    @Transactional
    public void delete(Long catId) {
        log.info("Запрос на удаления категории {}");
        Category category = getCategory(catId);
        Event event = eventRepository.findFirstByCategory(category);
        if (event == null) {
            categoryRepository.delete(category);
        } else throw new ConflictException("Данное категория  имеет привязку к событиям");
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("неверный Category ID"));
    }

}
