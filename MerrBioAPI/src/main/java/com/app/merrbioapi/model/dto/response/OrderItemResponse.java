package com.app.merrbioapi.model.dto.response;

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
public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productThumbnailUrl;
    private Double quantity;
    private Double price;
    private Double totalPrice;
    private UUID farmerId;
    private String farmerName;
    private String farmName;
}