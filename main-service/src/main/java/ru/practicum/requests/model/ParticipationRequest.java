package ru.practicum.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.model.Event;
import ru.practicum.requests.model.enums.RequestStatus;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
@Builder
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Column(name = "created")
    LocalDateTime created;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    RequestStatus status;
}
