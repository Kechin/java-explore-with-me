package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@AllArgsConstructor
@Validated
public class AdminCategoryController {

    CategoryServiceImpl categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    CategoryDto create(@Valid @RequestBody NewCategoryDto category) {
        return categoryService.create(category);
    }

    @PatchMapping("/{catId}")
    CategoryDto update(@RequestBody @Valid CategoryDto categoryDto, @PathVariable Long catId) {
        return categoryService.update(categoryDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}
