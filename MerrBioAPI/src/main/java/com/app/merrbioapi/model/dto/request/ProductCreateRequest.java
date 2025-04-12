package com.app.merrbioapi.model.dto.request;

import com.app.merrbioapi.model.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateRequest {
    private String name;
    private String description;
    private Double price;
    private Unit unit;
    private Double minAvailableQuantity;
    private Double maxAvailableQuantity;
    private Double minimumOrderQuantity;
    private List<UUID> categoryIds;
    private Boolean isOrganic;
    private String thumbnailUrl;
}