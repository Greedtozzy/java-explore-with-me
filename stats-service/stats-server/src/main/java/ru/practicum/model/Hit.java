package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stats", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "app", nullable = false)
    String app;

    @Column(name = "uri", nullable = false)
    String uri;

    @Column(name = "ip", nullable = false)
    String ip;

    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;
}
