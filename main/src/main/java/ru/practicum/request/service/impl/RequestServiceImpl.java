package ru.practicum.request.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.Dto.ParticipationRequestDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl  {
  private final RequestRepository requestRepository;
  private final UserRepository userRepository;
  private final EventRepository eventRepository;
    public List<ParticipationRequestDto> get (Long requesterId){
        return RequestMapper.toRequestDtos(requestRepository.getAllByRequester_Id(requesterId));
    }
    @Transactional
    public ParticipationRequestDto create(Long requesterId, Long eventId){
        log.info("Создание нового реквеста "+requesterId+" "+eventId);
        User requester=getUser(requesterId);
        Event event=getEvent(eventId);
        log.info("Получены Event User: "+event.getId() +" "+requester.getId());
        if (!event.getState().equals(State.PUBLISHED)){
           throw new ConflictException("Нельзя создать запрос на участие в неопубликованном событии.");
        }
        int confirmedReq = requestRepository.getAllByEvent_IdAndStatus(eventId,Status.CONFIRMED).size();
        if (  confirmedReq>=(event.getParticipantLimit())){
            throw new ConflictException("Лимит участников не может быть превышен.");
        }

        if (!requestRepository.getAllByEvent_IdAndAndRequester_Id(eventId,requesterId).isEmpty()){
            throw new ConflictException("Нельзя отправить запрос на участие более одного раза.");
        }
        if (event.getInitiator().getId().equals(requester.getId())){
            throw new ConflictException ("Нельзя создавать запрос на участие в своем событии");
        }
        log.info("Лимит участников {} - Всего учатников {}",event.getParticipantLimit(),event.getConfirmedRequests());
        Status status = event.getRequestModeration()?  Status.PENDING : Status.CONFIRMED;
        Request request= new Request(null, LocalDateTime.now(),event,requester,status);
        log.info("Реквест создан" );
        return RequestMapper.toPartRequestDto(requestRepository.save(request));
    }
//    @Transactional
//    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long requestId) {
//        Request request = getRequest(requestId);
//        Event event = getEvent(eventId);
//        if (event.getConfirmedRequests() < event.getParticipantLimit() ||
//                event.getRequestModeration() || event.getParticipantLimit() != 0) {
//            request.setStatus(Status.CONFIRMED);
//        }
//        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
//            List<Request> requests = requestRepository.getAllByEvent_IdAndStatus(eventId, Status.PENDING);
//            for (Request req : requests) {
//                req.setStatus(Status.CANCELED);
//            }
//        }
//        return RequestMapper.toPartRequestDto(request);
//    }




    @Transactional
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId){
        Request request =getRequest(requestId);
        getUser(requestId);
        if (!request.getRequester().getId().equals(requesterId)){
            throw new NotFoundException("");
        }
         request.setStatus(Status.CANCELED);
        return RequestMapper.toPartRequestDto(requestRepository.save(request));
    }

    private Request getRequest(Long requestId){
      return   requestRepository.findById(requestId)
              .orElseThrow(() ->new NotFoundException("Request c заданным ID не найден"));
    }
    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Event ID"));
    }

    private User getUser(Long id) {
        log.info(String.valueOf( userRepository.findAll().isEmpty()));
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }


}
