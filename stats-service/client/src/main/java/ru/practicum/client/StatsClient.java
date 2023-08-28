package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> saveStats(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    /*public ResponseEntity<Object> saveStats(HttpServletRequest request) {
    Не совсем понимаю, мы если в постмане тестируем, то сами тело запроса пишем на данном этапе.
    Зачем брать сервлет и вытаскивать реальный айпи. Или я чего то не понял)))
    мне показалось что такой вариант будет актуален когда основной сервис напишем и запросы на него
    кидать будем. Или я опять чего то не понял)) СЛОЖНА
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        return post("/hit", endpointHitDto);
    }*/

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        final String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        return get(path, parameters);
    }
}