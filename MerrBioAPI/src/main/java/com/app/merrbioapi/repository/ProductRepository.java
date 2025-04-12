package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByFarmerId(UUID farmerId);

    List<Product> findByIsOrganicTrue();

    List<Product> findByIsInStockTrue();

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchProducts(String keyword);

    @Query("SELECT p FROM Product p JOIN p.category pc JOIN pc.category c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(UUID categoryId);
}