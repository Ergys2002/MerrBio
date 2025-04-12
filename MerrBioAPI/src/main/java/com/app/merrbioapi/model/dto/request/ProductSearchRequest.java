package com.app.merrbioapi.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    // Filtering criteria
    private UUID farmerId;
    private List<UUID> categoryIds;
    private Boolean isOrganic;
    private Boolean showOutOfStock;
    private Double minPrice;
    private Double maxPrice;

    // Pagination parameters
    private Integer page;
    private Integer size;

    // Sorting parameters
    private String sortBy;
    private String sortDirection;
}