package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.response.FarmerResponse;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.repository.FarmerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private static final Logger log = LoggerFactory.getLogger(FarmerService.class);
    private final FarmerRepository farmerRepository;


    @Transactional(readOnly = true)
    public List<FarmerResponse> getFarmers(String farmName) {
        List<Farmer> farmers;

        if (StringUtils.hasText(farmName)) {
            log.debug("Fetching farmers with farm name containing: {}", farmName);
            farmers = farmerRepository.findByFarmNameContainingIgnoreCase(farmName);
        } else {
            log.debug("Fetching all farmers");
            farmers = farmerRepository.findAll();
        }

        return farmers.stream()
                .map(this::mapToFarmerResponse)
                .collect(Collectors.toList());
    }

    private FarmerResponse mapToFarmerResponse(Farmer farmer) {
        // Safely access nested properties
        String firstName = "N/A";
        String lastName = "N/A";
        if (farmer.getUser() != null && farmer.getUser().getUserInfo() != null) {
            UserInfo userInfo = farmer.getUser().getUserInfo();
            firstName = userInfo.getFirstName();
            lastName = userInfo.getLastName();
        } else {
            log.warn("Farmer with ID {} is missing associated User or UserInfo.", farmer.getId());
            // Handle this case as needed - maybe throw an error or return default values
            // For now, returning "N/A"
        }


        return FarmerResponse.builder()
                .id(farmer.getId())
                .farmName(farmer.getFarmName())
                .farmLocation(farmer.getFarmLocation())
                .bio(farmer.getBio())
                .isVerified(farmer.getIsVerified())
                .farmerFirstName(firstName)
                .farmerLastName(lastName)
                .build();
    }

}