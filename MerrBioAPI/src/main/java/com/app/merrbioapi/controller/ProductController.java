package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.request.ProductCreateMultipartRequest;
import com.app.merrbioapi.model.dto.request.ProductCreateRequest;
import com.app.merrbioapi.model.dto.request.ProductSearchRequest;
import com.app.merrbioapi.model.dto.request.ProductUpdateRequest;
import com.app.merrbioapi.model.dto.response.ProductResponse;
import com.app.merrbioapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product with images",
            description = "Create a new product with images and thumbnail (Farmer role required)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (requires FARMER role)")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Changed path from "/with-images" to base path
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<UUID> createProductWithImages(
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("price") Double price,
            @RequestPart("unit") String unit,
            @RequestPart("minAvailableQuantity") Double minAvailableQuantity,
            @RequestPart("maxAvailableQuantity") Double maxAvailableQuantity,
            @RequestPart(value = "minimumOrderQuantity", required = false) Double minimumOrderQuantity,
            @RequestPart(value = "categoryIds", required = false) List<UUID> categoryIds,
            @RequestPart(value = "isOrganic", required = false) Boolean isOrganic,
            @RequestPart(value = "thumbnail", required = false)
            @Parameter(description = "Thumbnail image file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile thumbnail,
            @RequestPart(value = "images", required = false)
            @Parameter(description = "Product image files", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            List<MultipartFile> images) {

        // Build the request object
        ProductCreateMultipartRequest request = ProductCreateMultipartRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .unit(com.app.merrbioapi.model.enums.Unit.valueOf(unit))
                .minAvailableQuantity(minAvailableQuantity)
                .maxAvailableQuantity(maxAvailableQuantity)
                .minimumOrderQuantity(minimumOrderQuantity)
                .categoryIds(categoryIds)
                .isOrganic(isOrganic != null ? isOrganic : false)
                .thumbnail(thumbnail)
                .images(images)
                .build();

        UUID productId = productService.createProductWithImages(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a product by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found", 
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable("id") UUID productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all available products")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get products by farmer", description = "Retrieve all products from a specific farmer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of products retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<ProductResponse>> getProductsByFarmer(
            @Parameter(description = "Farmer ID", required = true) @PathVariable UUID farmerId) {
        List<ProductResponse> products = productService.getProductsByFarmer(farmerId);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of products retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable UUID categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Search products", description = "Search products by keyword in name or description")
    @ApiResponse(responseCode = "200", description = "Search results",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @Parameter(description = "Search keyword", required = true) @RequestParam String keyword) {
        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Update product", description = "Update an existing product (Farmer role required)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized or not product owner"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable("id") UUID productId,
            @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(productId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete product", description = "Delete an existing product (Farmer role required)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized or not product owner"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable("id") UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Advanced product search", description = "Search and filter products with pagination and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results with pagination info",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @PostMapping("/advanced-search")
    public ResponseEntity<Page<ProductResponse>> advancedSearch(
            @Parameter(description = "Optional search keyword") @RequestParam(required = false) String query,
            @RequestBody(required = false) ProductSearchRequest searchRequest) {

        if (searchRequest == null) {
            searchRequest = new ProductSearchRequest();
        }

        Page<ProductResponse> results = productService.advancedSearch(query, searchRequest);
        return ResponseEntity.ok(results);
    }


}