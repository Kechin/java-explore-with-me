package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class StatDto {
    @Size(max=128)
    private String app;
    @Size(max=128)
    private String uri;
    @Size(max=128)
    private Long hits;
}
