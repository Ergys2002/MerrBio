package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.request.CategoryCreateRequest;
import com.app.merrbioapi.model.dto.response.CategoryResponse;
import com.app.merrbioapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create new categories", description = "Create multiple new categories (Admin role required)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categories created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (requires ADMIN or FARMER role)")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FARMER')")
    public ResponseEntity<Void> createCategories(@RequestBody List<CategoryCreateRequest> request) {
        categoryService.createCategories(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories, optionally filtered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    @PermitAll
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @Parameter(description = "Optional name filter (case-insensitive, partial match)", required = false)
            @RequestParam(required = false) String name
    ) {
        List<CategoryResponse> categories = categoryService.getCategories(name);
        return ResponseEntity.ok(categories);
    }
}
