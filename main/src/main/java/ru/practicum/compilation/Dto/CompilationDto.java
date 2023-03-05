package ru.practicum.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.event.Dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {

    List<EventShortDto> events;
    Long id;
    Boolean pinned;
    String title;
}
