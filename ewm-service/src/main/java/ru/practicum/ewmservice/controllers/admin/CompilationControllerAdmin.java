package ru.practicum.ewmservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmservice.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmservice.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("CompilationControllerAdmin POST create {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Valid UpdateCompilationRequest update,
                                 @PathVariable Long compId) {
        log.info("CompilationControllerAdmin PATCH update: {} id: {}", update, compId);
        return compilationService.update(compId, update);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("CompilationControllerAdmin delete {}", compId);
        compilationService.delete(compId);
    }
}
