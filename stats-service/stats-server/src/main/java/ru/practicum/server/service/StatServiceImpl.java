package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public EndpointHit saveStats(EndpointHitDto endpointHitDto) {
        log.info("StatServiceImpl: {}", endpointHitDto);
        return statRepository.save(EndpointHitMapper.fromDto(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("StatServiceImpl: end is after start");
        }
        log.info("StatServiceImpl start: {} end: {} uris: {} unique: {}", start, end, uris, unique);
        if (uris != null && uris.size() > 0) {
            return unique ? ViewStatsMapper.toDtoList(statRepository.getAllByUrisAndUniqueIp(start, end, uris))
                    : ViewStatsMapper.toDtoList(statRepository.getAllByUris(start, end, uris));
        } else {
            return unique ? ViewStatsMapper.toDtoList(statRepository.getAllByUniqueIp(start, end))
                    : ViewStatsMapper.toDtoList(statRepository.getAll(start, end));
        }
    }
}
