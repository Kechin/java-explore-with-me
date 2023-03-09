package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Service
public class HitSender extends BaseClient {
    private static final String apiPrefix = "/hit";
    private static final String getPrefix = "/stats?uris=/events/";

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
        return post(apiPrefix, hitDto);
    }


    public ResponseEntity getViews(Long eventId) {
        ResponseEntity request = get(getPrefix + eventId);

        return request;
    }

}



