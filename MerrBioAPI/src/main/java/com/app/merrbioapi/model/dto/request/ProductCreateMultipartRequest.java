package com.app.merrbioapi.model.dto.request;

import com.app.merrbioapi.model.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateMultipartRequest {
    private String name;
    private String description;
    private Double price;
    private Unit unit;
    private Double minAvailableQuantity;
    private Double maxAvailableQuantity;
    private Double minimumOrderQuantity;
    private List<UUID> categoryIds;
    private Boolean isOrganic;
    private MultipartFile thumbnail;
    private List<MultipartFile> images;
}