package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.request.CategoryCreateRequest;
import com.app.merrbioapi.model.dto.response.CategoryResponse;
import com.app.merrbioapi.model.entity.Category;
import com.app.merrbioapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategories(List<CategoryCreateRequest> requests) {
        List<Category> categoriesToSave = new ArrayList<>();
        for (CategoryCreateRequest request : requests) {

            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();
            categoriesToSave.add(category);
        }
        if (!categoriesToSave.isEmpty()) {
            categoryRepository.saveAll(categoriesToSave);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(String name) {
        List<Category> categories;

        // Check if the name filter is provided and not blank
        if (StringUtils.hasText(name)) {
            categories = categoryRepository.findByNameContainingIgnoreCase(name);
        } else {
            categories = categoryRepository.findAll();
        }

        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
