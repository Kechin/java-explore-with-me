package ru.practicum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {

    public static Hit hitDtoToHit(HitDto hitDto) {
        log.info(hitDto.toString());
        Hit newHit = new Hit(null, hitDto.getApp(), hitDto.getIp(), hitDto.getUri(), hitDto.getTimestamp());
        log.info("попытка создать Hit:  " + hitDto);
        return newHit;
    }
}
