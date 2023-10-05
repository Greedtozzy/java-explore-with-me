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
public class HitDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
