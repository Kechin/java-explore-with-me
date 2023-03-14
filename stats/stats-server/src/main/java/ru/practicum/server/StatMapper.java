package ru.practicum.server;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.StatDto;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class StatMapper {
    public static StatDto statToStatDto(Stat stat) {
        StatDto newStatDto = new StatDto(stat.getApp(), stat.getUri(), Math.toIntExact(stat.getHitsCount()));
        log.info("попытка создать StatDto: {} ", newStatDto);
        return newStatDto;
    }

    public static List<StatDto> statDtos(List<Stat> stats) {
        return stats.stream().map(StatMapper::statToStatDto).collect(Collectors.toList());
    }
}
