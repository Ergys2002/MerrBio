package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.request.CategoryCreateRequest;
import com.app.merrbioapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> createCategories(@RequestBody List<CategoryCreateRequest> request) {
        categoryService.createCategories(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
