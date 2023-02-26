package ru.practicum;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;


    @PostMapping("/hit")
    void create(@RequestBody HitDto hitDto) {
        statsService.addHit(hitDto);
    }

    @GetMapping("/stats")
    List<StatDto> getHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                          @RequestParam(required = false) List<String> uris,
                          @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("getHits from {} to {}, unique = {}, uris:{}", start, end, unique, uris);
        return statsService.getHits(start, end, uris, unique);
    }

}
