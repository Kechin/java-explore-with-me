package ru.practicum.main;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories
public class Server {
    @Generated
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);

    }

}
