package ru.practicum.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
