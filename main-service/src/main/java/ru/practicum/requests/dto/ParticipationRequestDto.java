package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.requests.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {
    LocalDateTime created;
    long event;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    long requester;
    RequestStatus status;
}
