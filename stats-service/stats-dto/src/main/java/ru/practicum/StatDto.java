package ru.practicum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class StatDto {
    String app;
    String uri;
    long hits;
}
