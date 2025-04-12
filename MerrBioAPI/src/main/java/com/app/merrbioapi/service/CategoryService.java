package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.request.CategoryCreateRequest;
import com.app.merrbioapi.model.entity.Category;
import com.app.merrbioapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategories(List<CategoryCreateRequest> request) {
        request.forEach(categoryCreateRequest -> {
            Category category = new Category();
            category.setName(categoryCreateRequest.getName());
            category.setDescription(categoryCreateRequest.getDescription());
            categoryRepository.save(category);
        });
    }
}
