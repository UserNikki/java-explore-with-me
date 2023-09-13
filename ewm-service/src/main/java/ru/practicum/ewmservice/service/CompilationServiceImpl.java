package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.mapper.CompilationMapper;
import ru.practicum.ewmservice.model.Compilation;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.repository.CompilationRepository;
import ru.practicum.ewmservice.repository.EventRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto compilationDto) {
        log.info("CompilationServiceImpl create CompilationDto: {}", compilationDto);
        final Compilation compilation = CompilationMapper.toModel(compilationDto);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDto.getEvents() != null) {
            final List<Event> getEvent = eventRepository.findAllById(compilationDto.getEvents());
            compilation.setEvents(getEvent);
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest update) {
        final Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String
                        .format("CompilationServiceImpl compilation id: '%d' not found", compId)));
        if (update.getEvents() != null) {
            compilation.setEvents(update.getEvents().stream()
                    .flatMap(ids -> eventRepository.findAllById(Collections.singleton(ids))
                            .stream())
                    .collect(Collectors.toList()));
        }
        compilation.setPinned(update.getPinned() != null ? update.getPinned() : compilation.getPinned());
        compilation.setTitle(update.getTitle() != null ? update.getTitle() : compilation.getTitle());
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String
                        .format("CompilationServiceImpl compilation id: '%d' not found", compId)));
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return compilationRepository.findAllByPinnedIs(pinned, pageable)
                .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        return CompilationMapper.toDto(compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String
                        .format("CompilationServiceImpl compilation id: '%d' not found", compId))));
    }

}
