package ru.practicum.client;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
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
public class HitSender extends BaseClient {
    private static final String apiPrefix = "/hit";
    private static final String getPrefix = "/stats?";
    private String url;


    public HitSender(String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .build()
        );
        url = serverUrl;
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
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        var response = request.get(url + getPrefix + requestUris);
        if (response == null || response.getBody().toString().isEmpty()) {
            return new ArrayList<>();
        }
        List<StatDto> resp = Arrays.asList(response.getBody().as(StatDto[].class));
        log.info("resp {}", resp);
        return resp;
    }


}





