package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsService {

    private final HitsRepository hitsRepository;


    public List<StatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return unique ? hitsRepository.getUniqueViewsWithoutUris(start, end) :
                    hitsRepository.getAllViewsWithoutUris(start, end);
        } else {
            return unique ? hitsRepository.getUniqueViews(start, end, uris) :
                    hitsRepository.getAllViews(start, end, uris);
        }
    }

    public void addHit(HitDto hitDto) {
        log.info("Попытка добавить новый hit");
        hitsRepository.save(HitMapper.hitDtoToHit(hitDto));
    }
}
