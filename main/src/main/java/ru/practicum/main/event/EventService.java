package ru.practicum.main.event;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.Dto.EventNewDto;
import ru.practicum.main.event.Dto.EventShortDto;
import ru.practicum.main.event.Dto.UpdateEventReq;
import ru.practicum.main.event.model.State;
import ru.practicum.main.event.Dto.EventFullDto;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.Dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface EventService {
    ///PUBLIC
    Set<EventShortDto> getAllWithFilter(String text, ArrayList<Long> cats, Boolean paid, LocalDateTime start,
                                        LocalDateTime end, Boolean onlyAvalable, String sort, Integer from,
                                        Integer size, HttpServletRequest httpServletRequest);

    ///PRIVATE
    Set<EventShortDto> getAll(Long userId, Integer from, Integer size);

    @Transactional
    EventFullDto updateByUser(UpdateEventReq eventDto, Long initiatorId, Long eventId);

    @Transactional
    EventFullDto updateByAdmin(UpdateEventReq event, Long eventId);

    @Transactional
    EventFullDto create(EventNewDto eventDto, Long initiatorId);

    //Получение события пользователем
    EventFullDto getByIdAndInitiatorId(Long eventId, Long initiatorId);

    EventFullDto getById(Long eventId);

    @Transactional
    EventFullDto update(UpdateEventReq updateEventReq, Long id);

    //Admin
    Set<EventFullDto> getEvents(List<Long> users, List<State> states, List<Long> categories,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Integer from, Integer size);

    @Transactional
    EventFullDto setCanceled(Long eventId);

    @Transactional
    EventFullDto setPublished(Long eventId);

    List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId);

    @Transactional
    EventRequestStatusUpdateResult requestsStatusUpdate(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest request);
}
