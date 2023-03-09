package ru.practicum.main.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.Dto.CompilationDto;
import ru.practicum.main.compilation.Dto.CompilationShortDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.model.CompilationMapper;
import ru.practicum.main.event.EventRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        return CompilationMapper.toCompilationDtos(compilationRepository.findAllByPinned(pageable, pinned).toList());
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = getCompilation(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public Compilation getCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Compilation ID"));
    }

    @Override
    @Transactional
    public CompilationDto create(CompilationShortDto compilation) {
        Set<Event> events = eventRepository.findAllByIdIn(compilation.getEvents());
        return CompilationMapper.toCompilationDto(compilationRepository.save(new Compilation((events),
                compilation.getTitle(), compilation.getPinned())));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, CompilationShortDto compilationDto) {
        Compilation compilation = getCompilation(compId);
        Set<Event> events = getEvents(compilationDto.getEvents());
        if (!events.isEmpty()) {
            compilation.setEvents(events);
        }
        compilation.setTitle(compilation.getTitle());
        compilation.setPinned(compilationDto.getPinned());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        compilationRepository.delete(getCompilation(catId));
    }

    private Set<Event> getEvents(List<Long> ids) {
        Set<Event> events = eventRepository.findAllByIdIn(ids);
        log.info("Получен список событий {}", events);
        return events;
    }
}
