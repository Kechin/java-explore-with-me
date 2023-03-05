package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.Dto.CompilationDto;
import ru.practicum.compilation.Dto.CompilationShortDto;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilitationService;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    CompilationDto create(@RequestBody CompilationShortDto compilation) {
        return compilitationService.create(compilation);
    }

    @PatchMapping(("/{compId}"))
    CompilationDto publish(@PathVariable Long compId, @RequestBody CompilationShortDto compilation) {
        return compilitationService.update(compId,compilation);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long catId) {
        compilitationService.delete(catId);
    }

}
