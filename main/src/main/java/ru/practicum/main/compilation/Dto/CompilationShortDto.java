package ru.practicum.main.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationShortDto {

    private List<Long> events;
    @NotNull
    private Boolean pinned;
    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @Size(groups = {Create.class},max = 1024)
    private String title;
}
