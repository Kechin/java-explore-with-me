package ru.practicum;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<StatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    @Transactional
    void addHit(HitDto hitDto);

    Integer getCountForUri(String uri);
}
