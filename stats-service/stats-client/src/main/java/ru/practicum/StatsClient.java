package ru.practicum;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsUri = "http://stats-server:9090";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public void hit(HitDto dto) {
        HttpEntity<HitDto> httpEntity = new HttpEntity<>(dto);
        restTemplate.exchange(statsUri + "/hit", HttpMethod.POST, httpEntity, Object.class);
    }

    public ResponseEntity<StatDto[]> get(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        Map<String, Object> params;
        String path;
        if (uris == null) {
            params = Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "unique", unique
            );
            path = statsUri + "/stats?start={strStart}&end={strEnd}&unique={unique}";
        } else {
            params = Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "uris", uris,
                    "unique", unique
            );
            path = statsUri + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        }
        return prepareGatewayResponse(restTemplate.getForEntity(path, StatDto[].class, params));
    }

    private static ResponseEntity<StatDto[]> prepareGatewayResponse(ResponseEntity<StatDto[]> response) {
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