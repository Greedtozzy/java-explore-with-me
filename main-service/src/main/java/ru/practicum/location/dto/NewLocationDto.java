package ru.practicum.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewLocationDto {
    @Min(value = -90)
    @Max(value = 90)
    Double lat;
    @Min(value = -180)
    @Max(value = 180)
    Double lon;
    @PositiveOrZero
    Double rad;
    @NotBlank
    String name;
}
