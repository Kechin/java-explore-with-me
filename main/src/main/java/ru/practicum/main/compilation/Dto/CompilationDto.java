package ru.practicum.main.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.main.Create;
import ru.practicum.main.event.Dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class CompilationDto {

    private Set<EventShortDto> events;
    private Long id;
    @NotBlank(groups = {Create.class})
    private Boolean pinned;
    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Size(groups = {Create.class},max = 1024)
    private String title;
}
