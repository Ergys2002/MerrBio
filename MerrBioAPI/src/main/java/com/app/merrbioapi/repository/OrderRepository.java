package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Order;
import com.app.merrbioapi.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Find orders by customer ID
    List<Order> findByCustomerId(UUID customerId);
    
    // Find orders by customer ID with pagination
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    
    // Find orders by farmer ID (through products)
    List<Order> findByOrderItemsProductFarmerId(UUID farmerId);
    
    // Find orders by farmer ID with pagination
    Page<Order> findByOrderItemsProductFarmerId(UUID farmerId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.product.farmer.id = :farmerId")
    Page<Order> findOrdersByFarmerId(@Param("farmerId") UUID farmerId, Pageable pageable);
    
    // Find orders by status
    List<Order> findByOrderStatus(OrderStatus status);
    
    // Find orders by customer ID and status
    List<Order> findByCustomerIdAndOrderStatus(UUID customerId, OrderStatus status);
    
    // Find orders by farmer ID and status
    List<Order> findByOrderItemsProductFarmerIdAndOrderStatus(UUID farmerId, OrderStatus status);
}