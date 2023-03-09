package ru.practicum.main;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RespToInt {
    public static Integer toHits(ResponseEntity response) {
        log.info("Преобразование статистики{}", response.getBody());
        if (response.getBody().toString().contains("hits")) {
            return Integer.parseInt(response.getBody().toString().split("=")[3].replaceAll("[^0-9_-]", ""));
        }
        return 0;
    }
}