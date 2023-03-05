package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitSender;
import ru.practicum.event.Dto.EventFullDto;
import ru.practicum.event.Dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {
    @Autowired
    private final HitSender hitSender;
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getWithFilters(@RequestParam(defaultValue = "") String text,
                                              @RequestParam(defaultValue = "0") ArrayList<Long> categories,
                                              @RequestParam(defaultValue = "false")
                                              boolean paid, @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime rangeStart,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "true")
                                              boolean onlyAvailable,
                                              @RequestParam(defaultValue = "EVENT_DATE")
                                              String sort,
                                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") int size) {

        log.info("Запрос на получения эвента с фильтрацией {}", text);
        return eventService.getAllWithFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {

        hitSender.createHit(httpServletRequest);
        return eventService.getById(eventId);
    }
}
