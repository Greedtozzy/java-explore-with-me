package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByEventId(long eventId);

    Integer countByEventIdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventIdAndRequesterId(long eventId, long userId);
}
