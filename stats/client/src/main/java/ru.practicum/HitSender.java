package ru.practicum.client.src.main.java.ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.HitDto;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j


@Service

public class HitSender extends BaseClient {
    private static final String API_PREFIX = "/hit";
    private static final String GET_VIEW_PREFIX = "/stats?uri=/events/";
    public static final @Value ("${app-name}") String APP_NAME = null;
    @Autowired
    public HitSender(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
        log.info("Попытка отправить HIT{}     {}",serverUrl,builder);

    }

    public ResponseEntity<Object> createHit(HttpServletRequest httpServletRequest) {
        HitDto hitDto = new HitDto(APP_NAME, httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), LocalDateTime.now());
                    log.info("Попытка создать app-name {}  hitDto {}",hitDto.getApp());
        return post(API_PREFIX, hitDto);
    }

    public Integer getViews(Long eventId) {
        Object hit =  get(GET_VIEW_PREFIX + eventId).getBody().toString();

        log.info("Получен ответ от сервера статистики {}", hit);
        return (Integer) hit;
    }

}



