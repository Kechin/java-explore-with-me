package ru.practicum.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stat {
    @Size(max = 128)
    private String app;
    @Size(max = 128)
    private String ip;
    @Size(max = 128)
    private String uri;
    @Size(max = 128)
    private Long hitsCount;
}
