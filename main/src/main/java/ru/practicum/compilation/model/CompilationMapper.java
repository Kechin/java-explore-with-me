package ru.practicum.compilation.model;


import lombok.experimental.UtilityClass;
import ru.practicum.compilation.Dto.CompilationDto;
import ru.practicum.event.model.EventMapper;

import java.util.List;
import java.util.stream.Collectors;
@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(EventMapper.toEventShortDtos(compilation.getEvents()), compilation.getId(),
                compilation.getPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> toCompilationDtos(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

}
