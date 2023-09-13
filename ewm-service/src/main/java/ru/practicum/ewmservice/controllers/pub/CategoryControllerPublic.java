package ru.practicum.ewmservice.controllers.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.category.CategoryDto;
import ru.practicum.ewmservice.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("CategoryControllerPublic GET getAll from: {}, size: {}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable @Min(1) Long catId) {
        log.info("CategoryControllerPublic GET getById categoryId: {}", catId);
        return categoryService.getById(catId);
    }

}
