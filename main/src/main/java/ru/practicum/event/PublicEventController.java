package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.src.main.java.ru.practicum.HitSender;
import ru.practicum.event.Dto.EventFullDto;
import ru.practicum.event.Dto.EventShortDto;

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
    @Autowired
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
        hitSender.createHit(httpServletRequest);
        return eventService.getAllWithFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, httpServletRequest);
    }

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        hitSender.createHit(httpServletRequest);
        return eventService.getById(eventId);
    }
}
