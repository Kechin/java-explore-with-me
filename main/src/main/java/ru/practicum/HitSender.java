package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j


@Service

public class HitSender extends BaseClient {
    private static final String API_PREFIX = "/hit";
    private static final String GET_VIEW_PREFIX = "/hit?uri=/events/";

    @Autowired
    public HitSender(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(HttpServletRequest httpServletRequest) {
        HitDto hitDto = new HitDto("main-service", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), LocalDateTime.now());
        return post(API_PREFIX, hitDto);
    }


    public Integer getViews(Long eventId) {
        Integer hit = (Integer) get(GET_VIEW_PREFIX + eventId).getBody();
        log.info("Получен ответ от сервера статистики {}", hit);
        return hit;
    }

//    public ResponseEntity<Object> createComment(Long itemId, Long userId, CommentDto commentDto) {
//        return post("/" + itemId + "/comment", userId, commentDto);
//    }


}



