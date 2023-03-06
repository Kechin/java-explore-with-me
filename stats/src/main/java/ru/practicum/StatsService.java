package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final HitsRepository hitsRepository;
    private final AppRepository appRepository;

    public List<StatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<Stat> stats;
        if (uris == null || uris.isEmpty()) {
            log.info("Get запрос без uris");
            stats = (unique ? hitsRepository.getUniqueViewsWithoutUris(start, end) :
                    hitsRepository.getAllViewsWithoutUris(start, end));
        } else {
            log.info("Get запрос c uris");
            stats = unique ? hitsRepository.getUniqueViews(start, end, uris) :
                    hitsRepository.getAllViews(start, end, uris);
        }

        return StatMapper.statDtos(stats);
    }

    @Transactional
    public void addHit(HitDto hitDto) {
        String appName = hitDto.getApp();
        if (!appRepository.existsById(appName)) {
            log.info("Попытка добавить новый App");
            appRepository.save(new App(appName));
        }
        ;
        log.info("Попытка добавить новый Hit");
        hitsRepository.save(HitMapper.hitDtoToHit(hitDto));
    }

    public Integer getCountForUri(String uri) {
        return hitsRepository.countAllByUri(uri);
    }
}
