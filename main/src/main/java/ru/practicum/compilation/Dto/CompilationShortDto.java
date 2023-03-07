package ru.practicum.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
    @Size(max=1024)
    private String title;
}
