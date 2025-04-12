package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByFarmerId(UUID farmerId);

    @Query("SELECT p FROM Product p JOIN p.category pc WHERE pc.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category pc " +
            "WHERE p.isInStock = true " +  // Always filter for in-stock products
            "AND (:query IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))) " +
            "AND (:farmerId IS NULL OR p.farmer.id = :farmerId) " +
            "AND (:categoryId IS NULL OR EXISTS (SELECT 1 FROM p.category pc2 WHERE pc2.category.id = :categoryId)) " +
            "AND (:isOrganic IS NULL OR p.isOrganic = :isOrganic) " +
            "AND (:showOutOfStock = true OR p.isInStock = true) " + // Optional override to show out-of-stock
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> advancedSearch(
            @Param("query") String query,
            @Param("farmerId") UUID farmerId,
            @Param("categoryId") UUID categoryId,
            @Param("isOrganic") Boolean isOrganic,
            @Param("showOutOfStock") Boolean showOutOfStock,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    // If you need to filter by multiple categories (any match), you can use this query
    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category pc " +
            "WHERE p.isInStock = true " +  // Always filter for in-stock products
            "AND (:query IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))) " +
            "AND (:farmerId IS NULL OR p.farmer.id = :farmerId) " +
            "AND (:#{#categoryIds == null || #categoryIds.isEmpty()} = true OR EXISTS (SELECT 1 FROM p.category pc2 WHERE pc2.category.id IN :categoryIds)) " +
            "AND (:isOrganic IS NULL OR p.isOrganic = :isOrganic) " +
            "AND (:showOutOfStock = true OR p.isInStock = true) " + // Optional override to show out-of-stock
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> advancedSearchMultipleCategories(
            @Param("query") String query,
            @Param("farmerId") UUID farmerId,
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("isOrganic") Boolean isOrganic,
            @Param("showOutOfStock") Boolean showOutOfStock,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
}