package ru.practicum.main.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.EventRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.State;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.Dto.ParticipationRequestDto;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestMapper;
import ru.practicum.main.request.model.Status;
import ru.practicum.main.user.UserRepository;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> get(Long requesterId) {
        return RequestMapper.toRequestDtos(requestRepository.getAllByRequester_Id(requesterId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long requesterId, Long eventId) {
        log.info("Создание нового реквеста {}  {}", requesterId, eventId);
        User requester = getUser(requesterId);
        Event event = getEvent(eventId);
        log.info("Получены Event User: {} {} ", event.getId(), requester.getId());
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя создать запрос на участие в неопубликованном событии.");
        }
        int confirmedReq = requestRepository.getAllByEvent_IdAndStatus(eventId, Status.CONFIRMED).size();
        if (event.getParticipantLimit() > 0 && confirmedReq >= (event.getParticipantLimit())) {
            throw new ConflictException("Лимит участников не может быть превышен.");
        }

        if (!requestRepository.getAllByEvent_IdAndAndRequester_Id(eventId, requesterId).isEmpty()) {
            throw new ConflictException("Нельзя отправить запрос на участие более одного раза.");
        }
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ConflictException("Нельзя создавать запрос на участие в своем событии");
        }
        log.info("Лимит участников {} ", event.getParticipantLimit());
        Status status = event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED;
        Request request = new Request(null, LocalDateTime.now(), event, requester, status);
        log.info("Реквест создан");
        return RequestMapper.toPartRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId) {
        Request request = getRequest(requestId);
        getUser(requestId);
        if (!request.getRequester().getId().equals(requesterId)) {
            throw new NotFoundException("User c данным ID не найден.");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toPartRequestDto(requestRepository.save(request));
    }

    private Request getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request c заданным ID не найден"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Event ID"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }


}
