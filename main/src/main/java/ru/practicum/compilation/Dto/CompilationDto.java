package ru.practicum.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.event.Dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

  private Set<EventShortDto> events;
    private Long id;
    @NotBlank
    private Boolean pinned;
    @NotBlank
    @Size(max=1024)
    private String title;
}
