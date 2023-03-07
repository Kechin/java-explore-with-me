package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
import ru.practicum.client.src.main.java.ru.practicum.HitSender;
import ru.practicum.event.Dto.EventFullDto;
import ru.practicum.event.Dto.EventNewDto;
import ru.practicum.event.Dto.EventShortDto;
import ru.practicum.event.Dto.UpdateEventReq;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.Dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.Dto.EventRequestStatusUpdateResult;
import ru.practicum.request.Dto.ParticipationRequestDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    @Autowired
    private final HitSender hitSender;

    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");

    ///PUBLIC
    @Override
    public Set<EventShortDto> getAllWithFilter(String text, ArrayList<Long> cats, Boolean paid, LocalDateTime start,
                                               LocalDateTime end, Boolean onlyAvalable, String sort, Integer from,
                                               Integer size, HttpServletRequest httpServletRequest) {
        List<Category> categories = categoryRepository.findAllById(cats);
        Sort sortValue = Sort.by(Sort.Direction.DESC, (Objects.equals(sort, "EVENT_DATE") ? "eventDate" : "views"));//EVENT_DATE, VIEWS
        Pageable pageable = PageRequest.of(from / size, size, sortValue);
        log.info("Запрос на поис эвентов cat:{}  \n ", cats);
        Set<Event> events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween(
                pageable, text, text, categories, start, end).toSet();
        Map<Long, List<Integer>> requestsAndViews = null;
        for (Event event : events) {
            requestsAndViews.put(event.getId(), List.of(event.getParticipantLimit(), hitSender.getViews(event.getId())));
        }
        Set<EventShortDto> eventDtos = EventMapper.toEventShortDtos(events);

        if (onlyAvalable) {
            eventDtos = eventDtos.stream().filter(x -> (x.getConfirmedRequests() < requestsAndViews
                    .get(x.getId()).get(0))).collect(Collectors.toSet());

        }
        for(EventShortDto e : eventDtos){
            e.setViews(requestsAndViews.get(e.getId()).get(1));
        }
        return eventDtos;
    }

    ///PRIVATE
    @Override
    public Set<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        Set<EventShortDto> events = EventMapper.toEventShortDtos((eventRepository.findAll(pageable)).toSet());
        return setViewsToShortDto(events);
    }

    @Override
    @Transactional
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
        if (eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Дата события должна быть в будущем.");
        }
        Category category = getCategory(eventDto.getCategory());
        log.info("Установка пользователя");
        Event event = EventMapper.eventNewDtoToEvent(eventDto);
        Location location = locationRepository
                .save(new Location(null, eventDto.getLocation().getLat(), eventDto.getLocation().getLon()));
        log.info("Событие создано {}", event.getAnnotation());
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
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
            return setView(EventMapper.toEventDto((event)));
        }
        throw new NotFoundException("Неверный запрос");
    }

    @Override
    public EventFullDto getById(Long eventId) {
        return setView(EventMapper.toEventDto(getEvent(eventId)));
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
        if (categoryId != null && !annotation.isBlank()) {
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
         EventFullDto eventFullDto= EventMapper.toEventDto(event);
         log.info("Вызов hitsender {}",eventFullDto.getId());
         eventFullDto.setViews(hitSender.getViews(eventFullDto.getId()));
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
        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1900, 1, 1, 1, 1);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(2030, 1, 1, 1, 1);
        }
        if (states == null) {
            states = List.of(State.values());
        }
        if (categories == null) {
            categories = categoryRepository.findAll().stream().map(e -> e.getId()).collect(Collectors.toList());
        }
        Set<Event> events = eventRepository.findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(
                pageable, users, states, categories, rangeStart, rangeEnd).toSet();
        return setViewsToFullDtos(EventMapper.toEventFullDtos(events));
    }

    @Override
    @Transactional
    public EventFullDto setCanceled(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
        }
        return setView(EventMapper.toEventDto(event));
    }

    @Override
    @Transactional
    public EventFullDto setPublished(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.REJECTED)) {
            throw new ConflictException("Данное событие было отменено и не может поменять статус.");
        }
        event.setState(State.PUBLISHED);
        return setView(EventMapper.toEventDto(event));
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

        if (!userId.equals(event.getInitiator().getId())) {
            throw new NotFoundException("Неверный запрос");
        }
        List<Request> requests = request.getRequestIds()
                .stream().map(this::getRequest)
                .peek(e -> {
                    if (e.getStatus().equals(Status.CONFIRMED) || request.getStatus().equals(Status.CANCELED)) {
                        throw new ConflictException("Нельзя отменить подтвержденную заявку на участие.");
                    }
                    if (requestRepository.getAllByEvent_IdAndStatus(eventId, Status.CONFIRMED).size() >=
                            (event.getParticipantLimit())) {
                        throw new ConflictException("Лимит участников не может быть превышен.");
                    } else {
                        e.setStatus(request.getStatus());
                    }
                })
                .collect(Collectors.toList());
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
    private Set<EventShortDto> setViewsToShortDto(Set<EventShortDto> events){
      return   events.stream().peek(e->e.setViews(hitSender.getViews(e.getId()))).collect(Collectors.toSet());
    }
    private EventFullDto setView(EventFullDto event){
        log.info("Вызо Hitsender");
        event.setViews(hitSender.getViews(event.getId()));
       return event;
    }
    private Set<EventFullDto> setViewsToFullDtos(Set<EventFullDto> events){
        return  events.stream().peek(e-> setView(e) ).collect(Collectors.toSet());
    }
}
