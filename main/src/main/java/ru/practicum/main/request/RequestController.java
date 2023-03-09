package ru.practicum.main.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.Dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    List<ParticipationRequestDto> getAllByUserId(@PathVariable Long userId) {
        return requestService.get(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Добавление запроса от текущего пользователя {} на участие в событии  {} ", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto update(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

}
