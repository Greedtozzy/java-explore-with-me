package ru.practicum.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompilationRequestDto {
    List<Long> events;
    Boolean pinned;
    @Length(max = 50)
    String title;
}
