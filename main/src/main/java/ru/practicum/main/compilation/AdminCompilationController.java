package ru.practicum.main.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.Create;
import ru.practicum.main.compilation.Dto.CompilationDto;
import ru.practicum.main.compilation.Dto.CompilationShortDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilitationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    CompilationDto create(@RequestBody @Validated({Create.class}) CompilationShortDto compilation) {
        return compilitationService.create(compilation);
    }

    @PatchMapping(("/{compId}"))
    CompilationDto publish(@PathVariable Long compId, @RequestBody @Valid CompilationShortDto compilation) {
        return compilitationService.update(compId, compilation);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long catId) {
        compilitationService.delete(catId);
    }

}
