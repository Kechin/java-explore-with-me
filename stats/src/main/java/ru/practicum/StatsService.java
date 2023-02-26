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


    public List<StatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> apps, Boolean unique) {

        return unique ? hitsRepository.getUniqueViews(start, end, apps) :
                hitsRepository.getAllViews(start, end, apps);
    }

    public void addHit(HitDto hitDto) {
        log.info("Попытка добавить новый hit");
        hitsRepository.save(HitMapper.hitDtoToHit(hitDto));
    }
}
