package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.category.CategoryDto;
import ru.practicum.ewmservice.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto categoryDto);

    CategoryDto getById(Long catId);

    CategoryDto update(Long catId, NewCategoryDto categoryDto);

    List<CategoryDto> getAll(Integer from, Integer size);

    void delete(Long catId);


}
