package ru.practicum.server;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.HitDto;

@Slf4j
@UtilityClass
public class HitMapper {

    public static Hit hitDtoToHit(HitDto hitDto) {
        log.info(hitDto.toString());
        Hit newHit = new Hit(null, new App(hitDto.getApp()), hitDto.getIp(), hitDto.getUri(), hitDto.getTimestamp());
        log.info("попытка создать Hit: {} ", hitDto);
        return newHit;
    }
}
