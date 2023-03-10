package ru.practicum.main.compilation;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.Dto.CompilationDto;
import ru.practicum.main.compilation.Dto.CompilationShortDto;
import ru.practicum.main.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long id);

    Compilation getCompilation(Long id);

    @Transactional
    CompilationDto create(CompilationShortDto compilation);

    @Transactional
    CompilationDto update(Long compId, CompilationShortDto compilationDto);

    @Transactional
    void delete(Long catId);
}
