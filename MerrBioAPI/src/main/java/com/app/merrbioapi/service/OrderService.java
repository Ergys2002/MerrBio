package com.app.merrbioapi.service;

import com.app.merrbioapi.exception.EntityNotFoundException;
import com.app.merrbioapi.model.dto.request.OrderCreateRequest;
import com.app.merrbioapi.model.dto.request.OrderItemRequest;
import com.app.merrbioapi.model.dto.response.OrderItemResponse;
import com.app.merrbioapi.model.dto.response.OrderResponse;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.Order;
import com.app.merrbioapi.model.entity.OrderItem;
import com.app.merrbioapi.model.entity.Product;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.OrderStatus;
import com.app.merrbioapi.model.event.OrderEvent;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.OrderItemRepository;
import com.app.merrbioapi.repository.OrderRepository;
import com.app.merrbioapi.repository.ProductRepository;
import com.app.merrbioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // Get the current authenticated user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Validate that there are items in the order
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        // Create a new order with PROCESSING status
        Order order = Order.builder()
                .customer(currentUser)
                .orderStatus(OrderStatus.PROCESSING)
                .notes(request.getNotes())
                .totalPrice(0.0) // Will be calculated based on items
                .build();
        
        order = orderRepository.save(order);
        
        // Process each order item
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Find the product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemRequest.getProductId()));
            
            // Check if product is in stock
            if (!product.getIsInStock()) {
                throw new IllegalArgumentException("Product is not in stock: " + product.getName());
            }
            
            // Check if quantity is valid
            if (itemRequest.getQuantity() < product.getMinimumOrderQuantity()) {
                throw new IllegalArgumentException("Minimum order quantity for " + product.getName() + " is " + product.getMinimumOrderQuantity());
            }
            
            if (product.getMaxAvailableQuantity() != null && itemRequest.getQuantity() > product.getMaxAvailableQuantity()) {
                throw new IllegalArgumentException("Maximum available quantity for " + product.getName() + " is " + product.getMaxAvailableQuantity());
            }
            
            // Create order item
            double itemPrice = product.getPrice() * itemRequest.getQuantity();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice()) // Store the price at the time of order
                    .build();
            
            orderItems.add(orderItemRepository.save(orderItem));
            totalPrice += itemPrice;
        }
        
        // Update the order with the total price
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        
        // Publish order created event
        OrderEvent orderEvent = OrderEvent.fromOrder(
                order, 
                null, 
                "Your order has been placed and is waiting for confirmation from the farmers."
        );
        eventPublisher.publishEvent(orderEvent);
        
        return mapToOrderResponse(order);
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
                
        // Check if the current user is authorized to view this order
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Allow access if user is the customer or the farmer of any product in the order
        boolean isCustomer = order.getCustomer().getId().equals(currentUser.getId());
        boolean isFarmerWithProductInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getFarmer().getUser().getId().equals(currentUser.getId()));
                
        if (!isCustomer && !isFarmerWithProductInOrder) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }
        
        return mapToOrderResponse(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCustomerOrders(int page, int size) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return orderRepository.findByCustomerId(currentUser.getId(), pageable)
                .map(this::mapToOrderResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderResponse> getFarmerOrders(int page, int size) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the Farmer entity associated with the current user
        Farmer farmer = farmerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("No farmer profile found for the current user"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Find orders using the farmer's ID (not the user's ID)
        return orderRepository.findOrdersByFarmerId(farmer.getId(), pageable)
                .map(this::mapToOrderResponse);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        
        // Verify this is being done by a farmer that has products in this order
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        boolean isFarmerWithProductInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getFarmer().getUser().getId().equals(currentUser.getId()));
                
        if (!isFarmerWithProductInOrder) {
            throw new AccessDeniedException("You do not have permission to update this order");
        }
        
        // Only allow transitioning from PROCESSING to either CONFIRMED or REJECTED
        if (order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order status can only be updated when in PROCESSING state");
        }
        
        if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.REJECTED) {
            throw new IllegalArgumentException("Order can only be updated to CONFIRMED or REJECTED status");
        }
        
        // Store old status for event
        OrderStatus oldStatus = order.getOrderStatus();
        
        // Update the order status
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        
        // Publish order status changed event
        String message = newStatus == OrderStatus.CONFIRMED ?
                "Your order has been confirmed by the farmer and is being processed." :
                "Your order has been rejected by the farmer.";
        
        OrderEvent orderEvent = OrderEvent.fromOrder(order, oldStatus, message);
        eventPublisher.publishEvent(orderEvent);
        
        return mapToOrderResponse(order);
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        
        // Get customer information
        User customer = order.getCustomer();
        UserInfo customerInfo = customer.getUserInfo();
        String customerName = customerInfo != null ? 
                (customerInfo.getFirstName() + " " + customerInfo.getLastName()) : 
                customer.getEmail();
        
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(customer.getId())
                .customerName(customerName)
                .customerEmail(customer.getEmail())
                .items(itemResponses)
                .totalPrice(order.getTotalPrice())
                .status(order.getOrderStatus())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        Farmer farmer = product.getFarmer();
        
        String farmerName = "Unknown";
        if (farmer.getUser() != null && farmer.getUser().getUserInfo() != null) {
            UserInfo farmerInfo = farmer.getUser().getUserInfo();
            farmerName = farmerInfo.getFirstName() + " " + farmerInfo.getLastName();
        }
        
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productThumbnailUrl(product.getThumbnailUrl())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .totalPrice(orderItem.getPrice() * orderItem.getQuantity())
                .farmerId(farmer.getId())
                .farmerName(farmerName)
                .farmName(farmer.getFarmName())
                .build();
    }
}