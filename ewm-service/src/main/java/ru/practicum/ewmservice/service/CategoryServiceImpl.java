package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.category.CategoryDto;
import ru.practicum.ewmservice.dto.category.NewCategoryDto;
import ru.practicum.ewmservice.exceptions.NotFoundException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.util.PageFactory.createPageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw new ValidationException("Category name: '" + categoryDto.getName() + "' is not unique");
        }
        log.info("CategoryServiceImpl create category dto: {}", categoryDto);
        final Category category = CategoryMapper.toModel(categoryDto);
        log.info("CategoryServiceImpl create category model {}", category);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getById(Long catId) {
        log.info("CategoryServiceImpl getById id: {}", catId);
        return CategoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id: '" + catId + "' not found")));
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto categoryDto) {
        final Category categoryToUpdate = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id: '" + catId + "' not found"));
        log.info("CategoryServiceImpl update id: {} json: {}", catId, categoryDto);
        if (categoryDto.getName() != null) {
            if (categoryToUpdate.getName().equals(categoryDto.getName()))
                throw new IllegalStateException("name already exist");
            categoryToUpdate.setName(categoryDto.getName());
        }
        return CategoryMapper.toDto(categoryRepository.save(categoryToUpdate));
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        final Pageable pageable = createPageable(from, size, Sort.Direction.ASC, "id");
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id: '" + catId + "' not found");
        }
        log.info("CategoryServiceImpl delete id: {}", catId);
        categoryRepository.deleteById(catId);
        log.info("Category id {} deleted  {}", catId, !categoryRepository.existsById(catId));
    }
}
