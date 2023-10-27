package ru.practicum.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminLocationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    Double lat;
    Double lon;
    Double rad;
    String name;
}
