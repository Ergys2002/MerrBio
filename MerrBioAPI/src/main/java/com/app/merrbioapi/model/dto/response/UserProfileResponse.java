package com.app.merrbioapi.model.dto.response;

import com.app.merrbioapi.model.enums.Gender;
import com.app.merrbioapi.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    // From User entity
    private UUID id;
    private String email;
    private Role role;

    // From UserInfo entity
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String phoneNumber;
    private Gender gender;

    // From Farmer entity (populated only if role is FARMER)
    private String farmName;
    private String farmLocation;
    private String bio;
    private Boolean isVerified; // Farmer verification status
}