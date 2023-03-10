package ru.practicum.main.event.model;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.main.category.CategoryMapper;
import ru.practicum.main.event.Dto.EventFullDto;
import ru.practicum.main.event.Dto.EventNewDto;
import ru.practicum.main.event.Dto.EventShortDto;
import ru.practicum.main.location.Location;
import ru.practicum.main.location.LocationMapper;
import ru.practicum.main.user.model.UserMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class EventMapper {
    public static EventFullDto toEventDto(Event event) {
        log.info("Преобразование эвента: {}", event.getId());
        return new EventFullDto(event.getId(), event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()), 0,
                event.getCreatedOn(), event.getDescription(), event.getEventDate(),
                UserMapper.toUserDto(event.getInitiator()), LocationMapper.toLocationDto(event.getLocation()), event.getPaid(),
                event.getParticipantLimit(), event.getPublishedOn(), event.getRequestModeration(),
                event.getState(), event.getTitle(), 0);

    }

    public static Event eventNewDtoToEvent(EventNewDto eventDto) {
        Location location = LocationMapper.toLocation(eventDto.getLocation());
        return new Event(null, eventDto.getAnnotation(), null, eventDto.getDescription(), null,
                eventDto.getPaid(), eventDto.getTitle(), eventDto.getEventDate(),
                location, eventDto.getParticipantLimit(), eventDto.getRequestModeration());
    }


    public static EventShortDto toShortDto(Event event) {
        return new EventShortDto(event.getId(), event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()),
                event.getDescription(), UserMapper.toUserDto(event.getInitiator()), event.getPaid(), event.getTitle(),
                0, 0, event.getEventDate());
    }

    public static Set<EventShortDto> toEventShortDtos(Set<Event> events) {
        return events.stream().map(EventMapper::toShortDto).collect(Collectors.toSet());
    }

    public static Set<EventFullDto> toEventFullDtos(Set<Event> events) {
        return events.stream().map(EventMapper::toEventDto).collect(Collectors.toSet());
    }
}
