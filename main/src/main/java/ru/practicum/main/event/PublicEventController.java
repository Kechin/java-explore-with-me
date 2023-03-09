package ru.practicum.main.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.HitSender;
import ru.practicum.main.event.Dto.EventShortDto;
import ru.practicum.main.event.Dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final HitSender hitSender;

    @GetMapping
    public Set<EventShortDto> getWithFilters(@RequestParam(defaultValue = "") String text,
                                             @RequestParam(defaultValue = "0") ArrayList<Long> categories,
                                             @RequestParam(defaultValue = "false")
                                             boolean paid, @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                             LocalDateTime rangeStart,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                             LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "false")
                                             boolean onlyAvailable,
                                             @RequestParam(defaultValue = "EVENT_DATE")
                                             String sort,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest httpServletRequest) {

        log.info("Запрос на получения эвента с фильтрацией {}", text);
        log.info("Запрос на создание hit {}", httpServletRequest.getRequestURI());
        hitSender.createHit(httpServletRequest);
        return eventService.getAllWithFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, httpServletRequest);
    }

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        log.info("Запрос на создание hit {}", httpServletRequest.getRequestURI());
        log.info("Попытка отправить HIT");
        hitSender.createHit(httpServletRequest);
        return eventService.getById(eventId);
    }
}
