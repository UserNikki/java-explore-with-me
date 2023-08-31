package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveStats(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("StatController POST /hit {}", endpointHitDto);
        return EndpointHitMapper.toDto(statService.saveStats(endpointHitDto));
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotEmpty @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime start,
                                       @NotEmpty @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("StatController GET /stats start {} end {} uris {} unique {}", start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }
}
