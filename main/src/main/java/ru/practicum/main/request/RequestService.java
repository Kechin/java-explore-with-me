package ru.practicum.main.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.request.Dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> get(Long requesterId);

    @Transactional
    ParticipationRequestDto create(Long requesterId, Long eventId);

    @Transactional
    ParticipationRequestDto cancelRequest(Long requesterId, Long requestId);
}
