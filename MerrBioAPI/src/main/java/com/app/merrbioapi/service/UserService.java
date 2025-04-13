package com.app.merrbioapi.service;

import com.app.merrbioapi.model.dto.response.UserProfileResponse;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.Role;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.UserInfoRepository;
import com.app.merrbioapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException; // Or use custom exception
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final FarmerRepository farmerRepository;
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserInfo not found for user ID: " + userId));

        UserProfileResponse.UserProfileResponseBuilder profileBuilder = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .birthDate(userInfo.getBirthDate())
                .phoneNumber(userInfo.getPhoneNumber())
                .gender(userInfo.getGender());

        if (user.getRole() == Role.FARMER) {
            Farmer farmer = farmerRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Farmer details not found for user ID: " + userId));
            profileBuilder
                    .farmName(farmer.getFarmName())
                    .farmLocation(farmer.getFarmLocation())
                    .bio(farmer.getBio())
                    .isVerified(farmer.getIsVerified());
        }

        return profileBuilder.build();
    }

    public Page<UserProfileResponse> getUsers(Integer page, Integer size, String search) {
        // Apply default values if parameters are null
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        // Default sort by createdAt in descending order (newest first)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> usersPage = userRepository.findBySearchTerm(search, pageable);

        return usersPage.map(this::mapToUserProfileResponse);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        // Implement mapping logic from User to UserProfileResponse
        UserInfo userInfo = user.getUserInfo();
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .birthDate(userInfo.getBirthDate())
                .phoneNumber(userInfo.getPhoneNumber())
                .gender(userInfo.getGender())
                .role(user.getRole())
                .build();
    }

}