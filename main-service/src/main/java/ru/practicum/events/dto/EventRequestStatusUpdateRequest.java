package ru.practicum.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.requests.model.enums.NewRequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    List<Long> requestIds;
    @NotNull
    NewRequestStatus status;
}
