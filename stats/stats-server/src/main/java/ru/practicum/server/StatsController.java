package ru.practicum.server;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;


    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    void create(@RequestBody HitDto hitDto) {
        log.info("Запрос на добавление Hit {}", hitDto);
        statsService.addHit(hitDto);
    }

    @GetMapping("/stats")
    List<StatDto> getHits(@RequestParam(defaultValue = "2020-05-05 00:00:00")
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                          @RequestParam(defaultValue = "2030-05-05 00:00:00")
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                          @RequestParam(required = false) List<String> uris,
                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("getHits from {} to {}, unique = {}, uris:{}", start, end, unique, uris);
        return statsService.getHits(start, end, uris, unique);
    }

}
