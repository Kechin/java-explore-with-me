package ru.practicum.request.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
   private List<ParticipationRequestDto> confirmedRequests;
  private   List<ParticipationRequestDto> rejectedRequests;
}
