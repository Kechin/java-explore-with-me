package ru.practicum.main.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.Dto.EventFullDto;
import ru.practicum.main.event.Dto.EventNewDto;
import ru.practicum.main.event.Dto.EventShortDto;
import ru.practicum.main.event.Dto.UpdateEventReq;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.Dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    Set<EventShortDto> getAllByUserId(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return eventService.getAll(userId, from, size);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody UpdateEventReq event) {
        log.info("Запрос на изменение событие пользователем");
        return eventService.updateByUser(event, userId, eventId);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    EventFullDto create(@PathVariable Long userId, @Valid @RequestBody EventNewDto event) {
        log.info("Запрос на добавление нового события");
        return eventService.create(event, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    EventFullDto getAllByUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение полной информации добавленой текущим пользователем");
        return eventService.getByIdAndInitiatorId(eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getAllRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult requestsStatusUpdate(@PathVariable Long userId, @PathVariable Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Запрос на изменение статуса реквеста {}", request);
        return eventService.requestsStatusUpdate(userId, eventId, request);
    }
}
