package ru.practicum.main.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.HitSender;
import ru.practicum.dto.StatDto;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.CategoryRepository;
import ru.practicum.main.event.Dto.EventFullDto;
import ru.practicum.main.event.Dto.EventNewDto;
import ru.practicum.main.event.Dto.EventShortDto;
import ru.practicum.main.event.Dto.UpdateEventReq;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventMapper;
import ru.practicum.main.event.model.State;
import ru.practicum.main.event.model.StateAction;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.location.Location;
import ru.practicum.main.location.LocationRepository;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.Dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.Dto.ParticipationRequestDto;
import ru.practicum.main.request.RequestRepository;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestMapper;
import ru.practicum.main.request.model.Status;
import ru.practicum.main.user.UserRepository;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");
    private final HitSender hitSender;

    ///PUBLIC
    public Set<EventShortDto> getAllWithFilter(String text, ArrayList<Long> cats, Boolean paid, LocalDateTime start,
                                               LocalDateTime end, Boolean onlyAvailable, String sort, Integer from,
                                               Integer size) {
        Sort sortValue = Sort.by(Sort.Direction.DESC, (Objects.equals(sort, "EVENT_DATE") ? "eventDate" : "views"));//EVENT_DATE, VIEWS
        Pageable pageable = PageRequest.of(from / size, size, sortValue);
        log.info("Запрос на поис эвентов cat:{}  \n ", cats);
        Set<Event> events = new HashSet<>();
        if (cats != null) {
            List<Category> category = categoryRepository.findAllById(cats);
            //TEXT PAID CAT
            if (text != null && paid != null)
                events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndPaidIsAndCategoryInAndEventDateBetween(
                        pageable, text, text, paid, category,
                        start, end).toSet();
                // PAID CAT
            else if (text == null && paid != null)
                events = eventRepository.findAllByPaidIsAndCategoryInAndEventDateBetween(
                        pageable, paid, category,
                        start, end).toSet();
                //TEXT  CAT
            else if (text != null && paid == null)
                events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween(
                        pageable, text, text, category,
                        start, end).toSet();
                // CAT
            else if (text == null && paid == null)
                events = eventRepository.findAllByCategoryInAndEventDateBetween(
                        pageable, category,
                        start, end).toSet();
        } else if (text != null && paid != null)
            //TEXT PAID
            events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndPaidIsAndEventDateBetween(
                    pageable, text, text, paid, start, end).toSet();
            //TEXT
        else if (text != null && paid == null)
            events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndEventDateBetween(
                    pageable, text, text, start, end).toSet();
        else if (text == null) {
            // PAID
            if (paid != null) events = eventRepository.findAllByPaidIsAndEventDateBetween(
                    pageable, paid, start, end).toSet();
                //
            else events = eventRepository.findAllByEventDateBetween(pageable, start, end).toSet();
        }

        Set<EventShortDto> eventDtos = EventMapper.toEventShortDtos(events);
        log.info("Установка views и confirmedeReq {}", eventDtos);
        if (!eventDtos.isEmpty())

            setViewsAndConfirmedRequestToShortDto(eventDtos);
        if (onlyAvailable) {
            Map<Long, Integer> idPartLimit = new HashMap<>();
            for (Event event : events) {
                idPartLimit.put(event.getId(), event.getParticipantLimit());
            }
            for (EventShortDto eventDto : eventDtos) {
                Integer partLimit = idPartLimit.get(eventDto.getId());
                if (partLimit != 0 && eventDto.getConfirmedRequests().equals(partLimit)) {
                    eventDtos.remove(eventDto);
                }
            }
        }

        setViewsAndConfirmedRequestToShortDto((eventDtos));

        return eventDtos;
    }

    ///PRIVATE
    @Override
    public Set<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        Set<EventShortDto> events = EventMapper.toEventShortDtos((eventRepository.findAll(pageable)).toSet());
        if (!events.isEmpty()) setViewsAndConfirmedRequestToShortDto(events);
        return events;
    }

    @Override
    public EventFullDto updateByUser(UpdateEventReq eventDto, Long initiatorId, Long eventId) {
        Event event = getEvent(eventId);
        if (eventDto.getEventDate() != null) {
            throw new ConflictException("Дата события должна быть в будущем.");
        }
        if (!initiatorId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Только создатель может менять запрос.");
        }
        return update(eventDto, eventId);
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(UpdateEventReq event, Long eventId) {

        return update(event, eventId);
    }

    @Override
    @Transactional
    public EventFullDto create(EventNewDto eventDto, Long initiatorId) {
        log.info("Попытка добавления нового события {}", eventDto);
        User user = getUser(initiatorId);
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата события должна быть в будущем.");
        }
        Category category = getCategory(eventDto.getCategory());
        log.info("Установка пользователя");
        Event event = EventMapper.eventNewDtoToEvent(eventDto);
        Location location = locationRepository
                .save(new Location(null, eventDto.getLocation().getLat(), eventDto.getLocation().getLon()));
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        log.info("Событие создано {}", event.getCreatedOn());
        log.info("Initiator установлен  {}", event.getInitiator().getName());
        EventFullDto evenDto = EventMapper.toEventDto(eventRepository.save(event));
        return evenDto;
    }

    //Получение события пользователем
    @Override
    public EventFullDto getByIdAndInitiatorId(Long eventId, Long initiatorId) {
        Event event = getEvent(eventId);
        log.info("Event: {}", event);
        if (initiatorId.equals(event.getInitiator().getId())) {
            EventFullDto eventFullDto = EventMapper.toEventDto(event);
            setViewsAndConfirmedRequestToFullDtos(Set.of(eventFullDto));
            return eventFullDto;
        }
        throw new NotFoundException("Неверный запрос");
    }

    @Override
    public EventFullDto getById(Long eventId) {
        EventFullDto eventFullDto = EventMapper.toEventDto(getEvent(eventId));
        setViewsAndConfirmedRequestToFullDtos(Set.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto update(UpdateEventReq updateEventReq, Long id) {
        log.info("Обновление эвента {}", updateEventReq);
        Event event = getEvent(id);
        String annotation = updateEventReq.getAnnotation();
        Long categoryId = updateEventReq.getCategory();
        String description = updateEventReq.getDescription();
        LocalDateTime eventDate = updateEventReq.getEventDate();
        Boolean paid = updateEventReq.getPaid();
        Integer participantLimit = updateEventReq.getParticipantLimit();
        Boolean requiredModeration = updateEventReq.getRequestModeration();
        StateAction stateAction = updateEventReq.getStateAction();
        String title = updateEventReq.getTitle();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (categoryId != null) {
            event.setCategory(getCategory(categoryId));
        }
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new ConflictException("Неверная дата события.");
            }
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (requiredModeration != null) {
            event.setRequestModeration(requiredModeration);
        }
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (event.getState().equals(State.REJECTED) || event.getState().equals(State.PUBLISHED)) {
                        throw new ConflictException("Нельзя опубликовать данное событие, текущий статус " +
                                event.getState());
                    }
                    event.setState(State.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(State.PUBLISHED)) {
                        throw new ConflictException("Данное событие было опубликовано и не может поменять статус.");
                    }
                    event.setState(State.REJECTED);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
            }
        }
        eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventDto(event);
        log.info("Вызов hitsender {}", eventFullDto.getId());
        setViewsAndConfirmedRequestToFullDtos(Set.of(eventFullDto));
        log.info("Событие обновлено {}", eventFullDto);
        return eventFullDto;
    }

    //Admin
    @Override
    public Set<EventFullDto> getEvents(List<Long> users, List<State> states, List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Попытка вызвать поиск.");
        Set<Event> events = new HashSet<>();
        if (states == null) {
            states = List.of(State.values());
        }
        if (users == null && categories == null) {
            events = eventRepository.findAllByStateInAndEventDateIsBetween(
                    pageable, states, rangeStart, rangeEnd).toSet();

        }
        if (users == null && categories != null) {
            events = eventRepository.findAllByStateInAndCategory_IdInAndEventDateIsBetween(
                    pageable, states, categories, rangeStart, rangeEnd).toSet();

        }
        if (users != null && categories == null) {
            events = eventRepository.findAllByInitiator_IdInAndStateInAndEventDateIsBetween(
                    pageable, users, states, rangeStart, rangeEnd).toSet();

        }
        if (users != null && categories != null) {
            events = eventRepository.findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(
                    pageable, users, states, categories, rangeStart, rangeEnd).toSet();

        }

        Set<EventFullDto> eventFullDtos = EventMapper.toEventFullDtos(events);
        if (!eventFullDtos.isEmpty()) setViewsAndConfirmedRequestToFullDtos(eventFullDtos);
        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto setCanceled(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
        }
        EventFullDto eventFullDto = EventMapper.toEventDto(event);
        setViewsAndConfirmedRequestToFullDtos(Set.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto setPublished(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.REJECTED)) {
            throw new ConflictException("Данное событие было отменено и не может поменять статус.");
        }
        event.setState(State.PUBLISHED);
        EventFullDto eventFullDto = EventMapper.toEventDto(event);
        setViewsAndConfirmedRequestToFullDtos(Set.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        return RequestMapper.toRequestDtos(requestRepository.getAllByEvent_Id(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult requestsStatusUpdate(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest request) {
        Event event = getEvent(eventId);
        Integer participantLimit = event.getParticipantLimit();
        Status status = request.getStatus();
        if (!userId.equals(event.getInitiator().getId())) {
            throw new NotFoundException("Неверный запрос");
        }
        Integer confirmeRequest = requestRepository.getConfirmedRequest(List.of(eventId)).get(eventId);
        Integer confirmedRequestsCount = confirmeRequest == null ? 0 : confirmeRequest;
        log.info("ConfirmedRequest--PartLimit {}--{}", confirmedRequestsCount, participantLimit);
        boolean overLimit = false;
        List<Request> requests = requestRepository.getAllByIdIn(request.getRequestIds());
        for (Request e : requests) {
            if (e.getStatus() == Status.CONFIRMED && status == Status.CANCELED) {
                throw new ConflictException("Нельзя отменить подтвержденную заявку на участие.");
            }
            if (confirmedRequestsCount.equals(participantLimit) && participantLimit != 0) {
                log.info("ConfReq - PartLimit {}-{}", confirmedRequestsCount, participantLimit);
                e.setStatus(Status.CANCELED);
                overLimit = true;
            } else {
                e.setStatus(status);
                if (status == Status.CONFIRMED) {
                    confirmedRequestsCount++;
                }
            }
        }
        requestRepository.saveAll(requests);
        if (overLimit) {
            throw new ConflictException("Лимит участников привышен");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (Request req : requests) {
            if (req.getStatus().equals(Status.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.toPartRequestDto(req));
            } else {
                rejectedRequests.add(RequestMapper.toPartRequestDto(req));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private Event getEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Event ID"));
        return event;
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Cat ID"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный User ID"));
    }

    private Request getRequest(Long id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("неверный Request ID"));
    }

    private void setViewsAndConfirmedRequestToFullDtos(Set<EventFullDto> events) {
        Map<String, EventFullDto> uriEvents = new HashMap<>();
        List<Long> eventsId = new ArrayList<>();
        for (EventFullDto event : events) {
            log.info("Event {}", event);
            eventsId.add(event.getId());
            uriEvents.put("/events/" + event.getId(), event);
        }
        Set<EventFullDto> resp = new HashSet<>();
        log.info("Запрос на сервер статистики {}", uriEvents);
        List<StatDto> statDtos;
        Map<Long, Integer> confRequests = (requestRepository.getConfirmedRequest(eventsId));

        statDtos = hitSender.getViews(uriEvents.keySet());
        for (StatDto statDto : statDtos) {
            String uri = statDto.getUri();
            EventFullDto event = uriEvents.get(uri);
            event.setViews(statDto.getHits());
            Integer confirmedRequest = confRequests.get(event.getId());
            event.setConfirmedRequests(confirmedRequest == null ? 0 : confirmedRequest);
            resp.add(event);
        }
        log.info("Статистика получена");
    }

    private void setViewsAndConfirmedRequestToShortDto(Set<EventShortDto> events) {
        Map<String, EventShortDto> uriEvents = new HashMap<>();
        List<Long> eventsId = new ArrayList<>();
        for (EventShortDto event : events) {
            log.info("Event {}", event);
            eventsId.add(event.getId());
            uriEvents.put("/events/" + event.getId(), event);
        }
        Set<EventShortDto> resp = new HashSet<>();
        List<StatDto> statDtos;
        log.info("Попытка вызвать HitSender");
        statDtos = hitSender.getViews(uriEvents.keySet());
        Map<Long, Integer> confRequests = (requestRepository.getConfirmedRequest(eventsId));
        for (StatDto statDto : statDtos) {
            String uri = statDto.getUri();
            EventShortDto event = uriEvents.get(uri);
            event.setViews(statDto.getHits());
            Integer confirmedRequest = confRequests.get(event.getId());
            event.setConfirmedRequests(confirmedRequest == null ? 0 : confirmedRequest);
            resp.add(event);
        }

    }
}
