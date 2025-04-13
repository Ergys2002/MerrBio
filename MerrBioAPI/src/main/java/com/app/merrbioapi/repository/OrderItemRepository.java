package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    // Find order items by order ID
    List<OrderItem> findByOrderId(UUID orderId);
    
    // Find order items by product ID
    List<OrderItem> findByProductId(UUID productId);
    
    // Delete order items by order ID
    void deleteByOrderId(UUID orderId);
    
    // Check if product is in any order
    boolean existsByProductId(UUID productId);
}