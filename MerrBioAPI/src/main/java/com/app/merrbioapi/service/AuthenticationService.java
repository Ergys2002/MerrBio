package com.app.merrbioapi.service;

import com.app.merrbioapi.config.JwtService;
import com.app.merrbioapi.exception.AccessDeniedException;
import com.app.merrbioapi.exception.EmailAlreadyExistsException;
import com.app.merrbioapi.exception.InvalidCredentialsException;
import com.app.merrbioapi.exception.PhoneNumberAlreadyExistsException;
import com.app.merrbioapi.exception.SessionNotFoundException;
import com.app.merrbioapi.exception.TokenRefreshException;
import com.app.merrbioapi.model.dto.request.AuthenticationRequest;
import com.app.merrbioapi.model.dto.request.BaseRegisterRequest;
import com.app.merrbioapi.model.dto.request.CustomerRegisterRequest;
import com.app.merrbioapi.model.dto.request.FarmerRegisterRequest;
import com.app.merrbioapi.model.dto.request.RegisterRequest;
import com.app.merrbioapi.model.dto.request.TokenRefreshRequest;
import com.app.merrbioapi.model.dto.response.AuthenticationResponse;
import com.app.merrbioapi.model.dto.response.DeviceSessionDto;
import com.app.merrbioapi.model.dto.response.RegisterResponse;
import com.app.merrbioapi.model.dto.response.TokenRefreshResponse;
import com.app.merrbioapi.model.entity.Farmer;
import com.app.merrbioapi.model.entity.RefreshToken;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.model.enums.Role;
import com.app.merrbioapi.repository.FarmerRepository;
import com.app.merrbioapi.repository.RefreshTokenRepository;
import com.app.merrbioapi.repository.UserInfoRepository;
import com.app.merrbioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FarmerRepository farmerRepository;

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UserDetails userDetails = authenticateAndGetUserDetails(request.getEmail(), request.getPassword());

        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthenticationResponse registerCustomer(CustomerRegisterRequest request) {
        // 1. Validate uniqueness
        checkEmailAndPhoneNumber(request.getEmail(), request.getPhoneNumber());

        // 2. Create User and UserInfo
        User savedUser = createUserAndInfo(request, Role.CUSTOMER);

        // 3. Generate tokens (registration implies login)
        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private User createUserAndInfo(BaseRegisterRequest request, Role role) {
        // Build User
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                // Initial empty list for tokens, etc.
                .build();
        // User needs to be saved first to get an ID for UserInfo association
        User savedUser = userRepository.save(user);

        // Build UserInfo
        UserInfo userInfo = UserInfo.builder()
                .user(savedUser) // Link to the saved user
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .build();
        userInfoRepository.save(userInfo);

        // Optional: Link UserInfo back to User if needed for immediate access,
        // though FetchType.LAZY is often preferred.
        // savedUser.setUserInfo(userInfo); // Be careful with bidirectional mapping management

        return savedUser;
    }


    @Transactional
    public AuthenticationResponse registerFarmer(FarmerRegisterRequest request) {
        // 1. Validate uniqueness
        checkEmailAndPhoneNumber(request.getEmail(), request.getPhoneNumber());
        // Optionally check farm name uniqueness if required
        // checkFarmNameExists(request.getFarmName());

        // 2. Create User and UserInfo
        User savedUser = createUserAndInfo(request, Role.FARMER);

        // 3. Create Farmer entity
        Farmer farmer = Farmer.builder()
                .user(savedUser)
                .farmName(request.getFarmName())
                .farmLocation(request.getFarmLocation())
                .bio(request.getBio())
                .isVerified(false) // Default verification status
                .build();
        farmerRepository.save(farmer);

        // 4. Generate tokens (registration implies login)
        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }


    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user);

                    refreshTokenService.revokeRefreshToken(requestRefreshToken);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                    return TokenRefreshResponse.builder()
                            .refreshToken(newRefreshToken.getToken())
                            .accessToken(newAccessToken)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found in database"));
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    @Transactional
    public void logoutAll(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            refreshTokenService.revokeAllUserTokens(user);
        }
    }

    @Transactional(readOnly = true)
    public List<DeviceSessionDto> getUserActiveSessions(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        List<RefreshToken> activeSessions = refreshTokenRepository.findActiveTokensByUser(user, Instant.now());
        return activeSessions.stream()
                .map(token -> new DeviceSessionDto(
                        token.getId(),
                        token.getCreatedAt(),
                        token.getExpiryDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void terminateSession(String email, UUID sessionId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        RefreshToken token = refreshTokenRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        if (!token.getUser().equals(user)) {
            throw new AccessDeniedException("You don't have permission to terminate this session");
        }

        refreshTokenService.revokeTokenById(sessionId);
    }
    private UserDetails authenticateAndGetUserDetails(String email, String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return (UserDetails) authentication.getPrincipal();
        } catch (AuthenticationException e){
            throw new InvalidCredentialsException();
        }
    }

    private User buildUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(Role.CUSTOMER);
        }

        return user;
    }

    private UserInfo buildUserInfo(RegisterRequest request, User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(request.getFirstName());
        userInfo.setLastName(request.getLastName());
        userInfo.setPhoneNumber(request.getPhoneNumber());
        userInfo.setBirthDate(request.getBirthDate());
        userInfo.setGender(request.getGender());
        userInfo.setUser(user);
        return userInfo;
    }
    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void checkEmailAndPhoneNumber(String email, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        if (userInfoRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException(phoneNumber);
        }
    }

    private void checkPhoneNumberExists(String phoneNumber) {
        if (userInfoRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException(phoneNumber);
        }
    }

    private void checkEmailAndPhoneNumber(RegisterRequest request) {
        checkEmailExists(request.getEmail());
        checkPhoneNumberExists(request.getPhoneNumber());
    }


}