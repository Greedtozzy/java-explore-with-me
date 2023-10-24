package ru.practicum.events.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.categories.model.Category;
import ru.practicum.events.model.enums.State;
import ru.practicum.location.model.Location;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events", schema = "public")
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "annotation", nullable = false)
    String annotation;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "title", nullable = false)
    String title;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    State state;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    Location location;
    @Column(name = "created", nullable = false)
    LocalDateTime created;
    @Column(name = "eventDate", nullable = false)
    LocalDateTime eventDate;
    @Column(name = "published_on", nullable = false)
    LocalDateTime publishedOn;
    @Column(name = "paid", nullable = false)
    boolean paid;
    @Column(name = "request_moderation", nullable = false)
    boolean requestModeration;
    @Column(name = "participant_limit", nullable = false)
    int participantLimit;
    @OneToMany(mappedBy = "event")
    List<ParticipationRequest> confirmedRequests;
    @Column(name = "views", columnDefinition = "int default 0")
    Integer views;
}
