package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatsClient {
    private final RestTemplate restTemplate;
    @Value("${app.statsUri}")
    private String statsUri;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate rest) {
        this.restTemplate = new RestTemplate();
    }

    public void hit(HitDto dto) {
        HttpEntity<HitDto> httpEntity = new HttpEntity<>(dto);
        restTemplate.exchange(statsUri + "/uri", HttpMethod.POST, httpEntity, Object.class);
    }

    public ResponseEntity<Object> get(String strStart, String strEnd, String[] uris, boolean unique) {
        Map<String, Object> params;
        String path;
        if (uris == null) {
            params = Map.of(
                    "start", LocalDateTime.parse(strStart, formatter),
                    "end", LocalDateTime.parse(strEnd, formatter),
                    "unique", unique
            );
            path = statsUri + "/stats?start={strStart}&end={strEnd}&unique={unique}";
        } else {
            params = Map.of(
                    "start", LocalDateTime.parse(strStart, formatter),
                    "end", LocalDateTime.parse(strEnd, formatter),
                    "uris", uris,
                    "unique", unique
            );
            path = statsUri + "/stats?start={strStart}&end={strEnd}&uris={uris}&unique={unique}";
        }
        return prepareGatewayResponse(restTemplate.getForEntity(path, Object.class, params));
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
