package ru.practicum.request.model;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.request.Dto.ParticipationRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RequestMapper {
    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return new Request(participationRequestDto.getId(), participationRequestDto.getCreated(), null, null, participationRequestDto.getStatus());
    }

    public static ParticipationRequestDto toPartRequestDto(Request request) {
        log.info("Преобразование в RequestDto");
        return new ParticipationRequestDto(request.getId(), request.getCreated(), request.getEvent().getId(),
                request.getRequester().getId(), request.getStatus());
    }

    public static List<ParticipationRequestDto> toRequestDtos(List<Request> requests) {
        return requests.stream().map(RequestMapper::toPartRequestDto).collect(Collectors.toList());

    }
}
