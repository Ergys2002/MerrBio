package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.request.ProductCreateMultipartRequest;
import com.app.merrbioapi.model.dto.request.ProductCreateRequest;
import com.app.merrbioapi.model.dto.request.ProductSearchRequest;
import com.app.merrbioapi.model.dto.request.ProductUpdateRequest;
import com.app.merrbioapi.model.dto.response.CategoryResponse;
import com.app.merrbioapi.model.dto.response.ProductResponse;
import com.app.merrbioapi.model.entity.Category;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.Image;
import com.app.merrbioapi.model.entity.Product;
import com.app.merrbioapi.model.entity.ProductCategory;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.repository.CategoryRepository;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.ImageRepository;
import com.app.merrbioapi.repository.ProductCategoryRepository;
import com.app.merrbioapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Transactional
    public UUID createProductWithImages(ProductCreateMultipartRequest request) {
        // Get current user (farmer)
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Farmer farmer = farmerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found for current user"));

        // Store thumbnail if provided
        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = fileService.storeFile(request.getThumbnail());
        }

        // Create the product
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
                .thumbnailUrl(thumbnailUrl)
                .category(new ArrayList<>())
                .imageUrls(new ArrayList<>())
                .build();

        // Save the product first to get an ID
        Product savedProduct = productRepository.save(product);

        // Add categories if provided
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

        // Process and save product images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String imageUrl = fileService.storeFile(imageFile);
                    if (imageUrl != null) {
                        Image image = Image.builder()
                                .imageUrl(imageUrl)
                                .product(savedProduct)
                                .build();
                        // Link the image back to the product (important for retrieval in mapToProductResponse)
                        savedProduct.getImageUrls().add(image); // Add the saved image entity
                        imageRepository.save(image);
                    }
                }
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

        // Consider deleting associated files (thumbnail, images) from storage
        if(product.getThumbnailUrl() != null) {
            // fileService.deleteFile(product.getThumbnailUrl()); // Assuming FileService has a delete method
        }
        if(product.getImageUrls() != null) {
            product.getImageUrls().forEach(image -> {
                // fileService.deleteFile(image.getImageUrl());
            });
        }
        // Deleting the product should cascade delete ProductCategory and Image entities if configured correctly.
        // If not, delete them manually first:
        // imageRepository.deleteByProductId(productId);
        // productCategoryRepository.deleteByProductId(productId);

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

    @Transactional(readOnly = true)
    public Page<ProductResponse> advancedSearch(String query, ProductSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;

        Sort sort = createSort(request);

        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean showOutOfStock = request.getShowOutOfStock() != null ? request.getShowOutOfStock() : false;

        Page<Product> productsPage;

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            productsPage = productRepository.advancedSearchMultipleCategories(
                    query,
                    request.getFarmerId(),
                    request.getCategoryIds(),
                    request.getIsOrganic(),
                    showOutOfStock,
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    pageable
            );
        } else {
            UUID categoryId = null;
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                categoryId = request.getCategoryIds().get(0);
            }

            productsPage = productRepository.advancedSearch(
                    query,
                    request.getFarmerId(),
                    categoryId,
                    request.getIsOrganic(),
                    showOutOfStock,
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    pageable
            );
        }

        return productsPage.map(this::mapToProductResponse);
    }

    private Sort createSort(ProductSearchRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";

        try {
            Product.class.getDeclaredField(sortBy);
        } catch (NoSuchFieldException e) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (request.getSortDirection() != null &&
                request.getSortDirection().equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, sortBy);
    }


}