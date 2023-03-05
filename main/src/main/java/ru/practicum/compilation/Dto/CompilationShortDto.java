package ru.practicum.compilation.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationShortDto {

    List<Long> events;
    Boolean pinned;
    String title;
}
