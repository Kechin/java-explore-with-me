package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitSender;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final HitSender statClient;
    private final Sort sortByDescEnd = Sort.by(Sort.Direction.DESC, "id");


///PUBLIC

    public List<EventShortDto> getAllWithFilter(String text, ArrayList<Long> cats, Boolean paid, LocalDateTime start,
                                                LocalDateTime end, Boolean onlyAvalable, String sort, Integer from,
                                                Integer size) {
        List<Category> categories = categoryRepository.findAllById(cats);
        Sort sortValue = Sort.by(Sort.Direction.DESC, (Objects.equals(sort, "EVENT_DATE") ? "eventDate" : "views"));//EVENT_DATE, VIEWS
        Pageable pageable = PageRequest.of(from / size, size, sortValue);
        log.info("Запрос на поис эвентов cat:{}  \n ", cats);
        List<Event> events = eventRepository.findAllByAnnotationContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndCategoryInAndEventDateBetween(pageable, text, text, categories, start, end).toList();
        setViews(events);
        if (onlyAvalable) {
            events = events.stream().filter(x -> (x.getConfirmedRequests() < x.getParticipantLimit()))
                    .collect(Collectors.toList());
        }
        return EventMapper.toEventShortDtos(events);


    }


///PRIVATE

    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, sortByDescEnd);
        return EventMapper.toEventShortDtos(setViews(eventRepository.findAll(pageable).toList()));

    }

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

    @Transactional
    public EventFullDto updateByAdmin(UpdateEventReq event, Long eventId) {

        return update(event, eventId);
    }

    @Transactional
    public EventFullDto create(EventNewDto eventDto, Long initiatorId) {
        log.info("Попытка добавления нового события" + eventDto);
        User user = getUser(initiatorId);
        if (eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Дата события должна быть в будущем.");
        }
        Category category = getCategory(eventDto.getCategory());
        log.info("Установка пользователя");
        Event event = EventMapper.eventNewDtoToEvent(eventDto);
        Location location = locationRepository
                .save(new Location(null, eventDto.getLocation().getLat(), eventDto.getLocation().getLon()));
        log.info("Событие создано" + event.getAnnotation());
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        event.setLocation(location);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        log.info("Initiator установлен" + event.getInitiator().getName());
        EventFullDto evenDto = EventMapper.toEventDto(event);
        log.info("ddd" + evenDto + evenDto.getId());
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    //Получение события пользователем
    public EventFullDto getByIdAndInitiatorId(Long eventId, Long initiatorId) {
        Event event = getEvent(eventId);
        log.info("Event: {}", event);
        if (initiatorId.equals(event.getInitiator().getId())) {
            return EventMapper.toEventDto(setView(event));
        }
        throw new NotFoundException("Неверный запрос");
    }

    public EventFullDto getById(Long eventId) {
        return EventMapper.toEventDto(getEvent(eventId));
    }


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
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (categoryId != null) {
            event.setCategory(getCategory(categoryId));
        }
        if (description != null) {
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
        if (title != null) {
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
        return EventMapper.toEventDto(event);

    }


    //Admin
    public List<EventFullDto> getEvents(List<Long> users, List<State> states, List<Long> categories,
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
        List<Event> events = eventRepository.findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween
                (pageable, users, states, categories, rangeStart, rangeEnd).toList();
        return EventMapper.toEventFullDtos(setViews(events));

    }

    @Transactional
    public EventFullDto setCanceled(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
        }

        return EventMapper.toEventDto(event);
    }


    @Transactional
    public EventFullDto setPublished(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.REJECTED)) {
            throw new ConflictException("Данное событие было отменено и не может поменять статус.");
        }
        event.setState(State.PUBLISHED);
        return EventMapper.toEventDto(event);
    }

    public List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        return RequestMapper.toRequestDtos(requestRepository.getAllByEvent_Id(eventId));
    }


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
        return setView(event);
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

    private Event setView(Event event) {
        event.setViews(statClient.getViews(event.getId()));
        return event;
    }

    private List<Event> setViews(List<Event> event) {
        return event.stream().map(this::setView).collect(Collectors.toList());

    }
}
