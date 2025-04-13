package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.request.OrderCreateRequest;
import com.app.merrbioapi.model.dto.response.OrderResponse;
import com.app.merrbioapi.model.enums.OrderStatus;
import com.app.merrbioapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Create a new order", description = "Create a new order with selected products. Only customers can create orders.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = { @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = OrderResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - requires customer role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID. User must be either the customer who placed the order or a farmer with products in the order.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not authorized to view this order"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get customer's orders", description = "Retrieve all orders placed by the authenticated customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - requires customer role")
    })
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getCustomerOrders(page, size));
    }

    @GetMapping("/farmer-orders")
    @PreAuthorize("hasAuthority('FARMER')")
    @Operation(summary = "Get farmer's orders", description = "Retrieve all orders containing products from the authenticated farmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmer orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - requires farmer role")
    })
    public ResponseEntity<Page<OrderResponse>> getFarmerOrders(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getFarmerOrders(page, size));
    }

    @PutMapping("/{orderId}/accept")
    @PreAuthorize("hasAuthority('FARMER')")
    @Operation(summary = "Accept order", description = "Accept an order as a farmer. Only the farmer whose products are in the order can accept it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order accepted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - requires farmer role"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not a farmer with products in this order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid order status change")
    })
    public ResponseEntity<OrderResponse> acceptOrder(
            @Parameter(description = "ID of the order to accept", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED));
    }

    @PutMapping("/{orderId}/reject")
    @PreAuthorize("hasAuthority('FARMER')")
    @Operation(summary = "Reject order", description = "Reject an order as a farmer. Only the farmer whose products are in the order can reject it.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order rejected successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - requires farmer role"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not a farmer with products in this order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid order status change")
    })
    public ResponseEntity<OrderResponse> rejectOrder(
            @Parameter(description = "ID of the order to reject", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, OrderStatus.REJECTED));
    }
}