package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilation(@RequestParam(required = false) boolean pinned,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("CompilationControllerPublic GET getCompilation pinned: {} from: {} size: {}",
                pinned, from, size);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("CompilationControllerPublic GET getCompilationById id: {}", compId);
        return compilationService.getById(compId);
    }
}
