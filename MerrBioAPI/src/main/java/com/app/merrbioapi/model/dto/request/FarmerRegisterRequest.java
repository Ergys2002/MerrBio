package com.app.merrbioapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // Important for Lombok with inheritance
public class FarmerRegisterRequest extends BaseRegisterRequest {

    @NotBlank(message = "Farm name cannot be blank")
    private String farmName;

    @NotBlank(message = "Farm location cannot be blank")
    private String farmLocation;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio; // Optional bio
}