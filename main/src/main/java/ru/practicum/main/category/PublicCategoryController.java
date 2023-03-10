package ru.practicum.main.category;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {

    private final CategoryServiceImpl categoryService;

    @GetMapping()
    List<CategoryDto> get(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                          @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
