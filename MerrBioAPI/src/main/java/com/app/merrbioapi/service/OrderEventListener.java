package com.app.merrbioapi.service;

import com.app.merrbioapi.model.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    
    private final NotificationService notificationService;
    
    @Async
    @EventListener
    public void handleOrderEvent(OrderEvent event) {
        log.info("Processing order event: {} -> {}, Order ID: {}", 
                event.getOldStatus(), event.getNewStatus(), event.getOrderId());
        
        // Send notification to the customer
        notificationService.sendNotificationToUser(
                event.getCustomerId(),
                "Order Status Update",
                event.getMessage()
        );
        
        // Additional logic can be added here for different types of notifications
        // based on the status change, such as email notifications, etc.
    }
}