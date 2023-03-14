package ru.practicum.main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.HitSender;

@Configuration
public class HitConfig {
    @Value("${stat-server.url}")
    private String url;

    @Bean
    HitSender hitSender() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new HitSender(url, builder);
    }
}
