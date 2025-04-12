package com.app.merrbioapi.model.dto.response;

import com.app.merrbioapi.model.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID farmerId;
    private String farmerName;
    private String farmLocation;
    private Double price;
    private Unit unit;
    private Double minAvailableQuantity;
    private Double maxAvailableQuantity;
    private Double minimumOrderQuantity;
    private List<CategoryResponse> categories;
    private Boolean isOrganic;
    private Boolean isInStock;
    private List<String> imageUrls;
    private String thumbnailUrl;
    private Instant createdAt;
    private Instant updatedAt;
}