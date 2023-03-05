package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.CategoryMapper;
import ru.practicum.event.Dto.EventFullDto;
import ru.practicum.event.Dto.EventNewDto;
import ru.practicum.event.Dto.EventShortDto;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static EventFullDto toEventDto(Event event) {
        log.info("Преобразование эвента: " + event.getId());
        return new EventFullDto(event.getId(), event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()), event.getConfirmedRequests(),
                event.getCreatedOn(), event.getDescription(), event.getEventDate(),
                UserMapper.toUserDto(event.getInitiator()), LocationMapper.toLocationDto(event.getLocation()), event.getPaid(),
                event.getParticipantLimit(), event.getPublishedOn(), event.getRequestModeration(),
                event.getState(), event.getTitle(), event.getViews());

    }

    public static Event eventNewDtoToEvent(EventNewDto eventDto) {
        Location location = LocationMapper.toLocation(eventDto.getLocation());
        return new Event(null, eventDto.getAnnotation(), null, eventDto.getDescription(), null,
                eventDto.getPaid(), eventDto.getTitle(), 0, 0, eventDto.getEventDate(), location
                , eventDto.getParticipantLimit(), eventDto.getRequestModeration());
    }


    public static EventShortDto toShortDto(Event event) {
        return new EventShortDto(event.getId(), event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()),
                event.getDescription(), UserMapper.toUserDto(event.getInitiator()), event.getPaid(), event.getTitle(),
                event.getViews(), event.getConfirmedRequests(), event.getEventDate());
    }

    public static List<EventShortDto> toEventShortDtos(List<Event> events) {
        return events.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    public static List<EventFullDto> toEventFullDtos(List<Event> events) {
        return events.stream().map(EventMapper::toEventDto).collect(Collectors.toList());
    }
}
