package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.Dto.CompilationDto;
import ru.practicum.compilation.Dto.CompilationShortDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)

public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        return CompilationMapper.toCompilationDtos(compilationRepository.findAllByPinned(pageable, pinned).toList());
    }

    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = getCompilation(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Compilation ID"));
    }

    @Transactional
    public CompilationDto create(CompilationShortDto compilation) {
        List<Event> events = eventRepository.findAllByIdIn(compilation.getEvents());
        if (events == null) {
            events = new ArrayList<>();
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(new Compilation((events),
                compilation.getTitle(), compilation.getPinned())));
    }

    private List<Event> getEvents(List<Long> ids) {
        List<Event> events = eventRepository.findAllByIdIn(ids);
        if (events == null) new NotFoundException("Неверный Event ID");
        log.info("Получен список событий {}", events);

        return events;
    }

    @Transactional
    public CompilationDto update(Long compId, CompilationShortDto compilationDto) {
        Compilation compilation = getCompilation(compId);
        compilation.setEvents(getEvents(compilationDto.getEvents()));
        compilation.setTitle(compilation.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void delete(Long catId) {
        compilationRepository.delete(getCompilation(catId));
    }
}
