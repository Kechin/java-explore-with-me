package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stat {
    private String app;
    private String ip;
    private String uri;

    private Long hitsCount;
}
