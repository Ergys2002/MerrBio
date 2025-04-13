package com.app.merrbioapi.model.event;

import com.app.merrbioapi.model.entity.Order;
import com.app.merrbioapi.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {
    private UUID orderId;
    private UUID customerId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String message;

    public static OrderEvent fromOrder(Order order, OrderStatus oldStatus, String message) {
        return OrderEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .oldStatus(oldStatus)
                .newStatus(order.getOrderStatus())
                .message(message)
                .build();
    }
}