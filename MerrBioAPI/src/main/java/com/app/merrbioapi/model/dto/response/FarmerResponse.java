package com.app.merrbioapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerResponse {
    private UUID id; // Farmer ID
    private String farmName;
    private String farmLocation;
    private String bio;
    private Boolean isVerified;
    private String farmerFirstName; // Get from related UserInfo
    private String farmerLastName;  // Get from related UserInfo
    // private UUID userId; // Optionally include User ID if needed
}