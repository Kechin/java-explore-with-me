package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class HitSender extends BaseClient {
    private static final String apiPrefix = "/hit";
    private static final String getPrefix = "/stats?";

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

    public List<StatDto> getViews(Set<String> ids) {
        StringBuilder requestUris = new StringBuilder(getPrefix);
        for (String uri : ids) {
            requestUris.append("&uris=" + uri);
        }
        var response = get(requestUris.toString());
        ObjectMapper mapper = new ObjectMapper();
        List<StatDto> stats = new ArrayList<>();
        log.info("{}", response.getBody());
        try {
            stats = Arrays.asList(mapper.readValue(response.getBody().toString(), StatDto[].class));
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
        return stats;
    }
}





