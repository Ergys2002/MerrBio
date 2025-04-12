package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.request.ProductCreateRequest;
import com.app.merrbioapi.model.dto.request.ProductUpdateRequest;
import com.app.merrbioapi.model.dto.response.CategoryResponse;
import com.app.merrbioapi.model.dto.response.ProductResponse;
import com.app.merrbioapi.model.entity.Category;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.Product;
import com.app.merrbioapi.model.entity.ProductCategory;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.repository.CategoryRepository;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.ProductCategoryRepository;
import com.app.merrbioapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final FarmerRepository farmerRepository;

    @Transactional
    public UUID createProduct(ProductCreateRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Farmer farmer = farmerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found for current user"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .farmer(farmer)
                .price(request.getPrice())
                .unit(request.getUnit())
                .minAvailableQuantity(request.getMinAvailableQuantity())
                .maxAvailableQuantity(request.getMaxAvailableQuantity())
                .minimumOrderQuantity(request.getMinimumOrderQuantity())
                .isOrganic(request.getIsOrganic())
                .isInStock(true)
                .thumbnailUrl(request.getThumbnailUrl())
                .category(new ArrayList<>())
                .build();

        Product savedProduct = productRepository.save(product);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            for (Category category : categories) {
                ProductCategory productCategory = ProductCategory.builder()
                        .product(savedProduct)
                        .category(category)
                        .build();
                productCategoryRepository.save(productCategory);
            }
        }

        return savedProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByFarmer(UUID farmerId) {
        return productRepository.findByFarmerId(farmerId).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Farmer farmer = farmerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found for current user"));

        // Verify that the current farmer owns this product
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new SecurityException("You do not have permission to update this product");
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getUnit() != null) {
            product.setUnit(request.getUnit());
        }

        if (request.getMinAvailableQuantity() != null) {
            product.setMinAvailableQuantity(request.getMinAvailableQuantity());
        }

        if (request.getMaxAvailableQuantity() != null) {
            product.setMaxAvailableQuantity(request.getMaxAvailableQuantity());
        }

        if (request.getMinimumOrderQuantity() != null) {
            product.setMinimumOrderQuantity(request.getMinimumOrderQuantity());
        }

        if (request.getIsOrganic() != null) {
            product.setIsOrganic(request.getIsOrganic());
        }

        if (request.getIsInStock() != null) {
            product.setIsInStock(request.getIsInStock());
        }

        if (request.getThumbnailUrl() != null) {
            product.setThumbnailUrl(request.getThumbnailUrl());
        }

        productRepository.save(product);

        if (request.getCategoryIds() != null) {
            productCategoryRepository.deleteByProductId(product.getId());

            // Add new categories
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            for (Category category : categories) {
                ProductCategory productCategory = ProductCategory.builder()
                        .product(product)
                        .category(category)
                        .build();
                productCategoryRepository.save(productCategory);
            }
        }
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Farmer farmer = farmerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found for current user"));

        // Verify that the current farmer owns this product
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new SecurityException("You do not have permission to delete this product");
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        List<CategoryResponse> categories = product.getCategory().stream()
                .map(productCategory -> CategoryResponse.builder()
                        .id(productCategory.getCategory().getId())
                        .name(productCategory.getCategory().getName())
                        .description(productCategory.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());

        List<String> imageUrls = product.getImageUrls().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .farmerId(product.getFarmer().getId())
                .farmerName(product.getFarmer().getUser().getUserInfo().getFirstName() + " " +
                        product.getFarmer().getUser().getUserInfo().getLastName())
                .farmLocation(product.getFarmer().getFarmLocation())
                .price(product.getPrice())
                .unit(product.getUnit())
                .minAvailableQuantity(product.getMinAvailableQuantity())
                .maxAvailableQuantity(product.getMaxAvailableQuantity())
                .minimumOrderQuantity(product.getMinimumOrderQuantity())
                .categories(categories)
                .isOrganic(product.getIsOrganic())
                .isInStock(product.getIsInStock())
                .imageUrls(imageUrls)
                .thumbnailUrl(product.getThumbnailUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}