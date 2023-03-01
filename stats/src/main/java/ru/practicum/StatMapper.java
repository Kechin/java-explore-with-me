package ru.practicum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatMapper {
    public static StatDto statToStatDto(Stat stat) {
        StatDto newStatDto = new StatDto(stat.getApp(), stat.getUri(), stat.getHitsCount());
        log.info("попытка создать StatDto: {} ", newStatDto);
        return newStatDto;
    }
    public static List<StatDto> statDtos(List<Stat> stats){
        return stats.stream().map(StatMapper::statToStatDto).collect(Collectors.toList());
    }


}