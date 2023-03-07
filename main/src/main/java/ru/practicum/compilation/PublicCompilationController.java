package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.Dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService compilitationService;

    @GetMapping
    List<CompilationDto> get(@RequestParam(defaultValue = "true") Boolean pinned,
                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        return compilitationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    CompilationDto get(@PathVariable Long compId) {

        return compilitationService.getCompilationById(compId);
    }

}
