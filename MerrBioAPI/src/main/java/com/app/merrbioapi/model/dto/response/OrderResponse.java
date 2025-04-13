package com.app.merrbioapi.model.dto.response;

import com.app.merrbioapi.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private List<OrderItemResponse> items;
    private Double totalPrice;
    private OrderStatus status;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}