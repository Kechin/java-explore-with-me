package ru.practicum.request.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.request.model.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    Status status;
}
