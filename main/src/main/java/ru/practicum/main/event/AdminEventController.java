package ru.practicum.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.Dto.EventFullDto;
import ru.practicum.main.event.Dto.UpdateEventReq;
import ru.practicum.main.event.model.State;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    Set<EventFullDto> get(@RequestParam(required = false) ArrayList<Long> users,
                          @RequestParam(required = false) ArrayList<State> states,
                          @RequestParam(required = false) ArrayList<Long> categories,
                          @RequestParam(defaultValue = "2000-01-01 01:01:01")
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                          @RequestParam(defaultValue = "2050-01-01 01:01:01")
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                          @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    EventFullDto update(@PathVariable Long eventId, @RequestBody UpdateEventReq event) {
        return eventService.updateByAdmin(event, eventId);
    }

    @PatchMapping(("/{eventId}/publish"))
    EventFullDto publish(@PathVariable Long eventId) {
        return eventService.setPublished(eventId);
    }

    @PatchMapping(("/{eventId}/reject"))
    EventFullDto canceled(@PathVariable Long eventId) {
        return eventService.setCanceled(eventId);
    }
}
