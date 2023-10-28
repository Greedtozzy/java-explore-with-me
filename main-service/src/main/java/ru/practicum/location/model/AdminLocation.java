package ru.practicum.location.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "administrated_locations")
@Builder
public class AdminLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "lat", nullable = false)
    double lat;
    @Column(name = "lon", nullable = false)
    double lon;
    @Column(name = "rad", nullable = false)
    double rad;
    @Column(name = "name", nullable = false)
    String name;
}
