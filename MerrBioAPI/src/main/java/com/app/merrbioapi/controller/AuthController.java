package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.request.AuthenticationRequest;
import com.app.merrbioapi.model.dto.request.CustomerRegisterRequest;
import com.app.merrbioapi.model.dto.request.FarmerRegisterRequest;
import com.app.merrbioapi.model.dto.request.RegisterRequest;
import com.app.merrbioapi.model.dto.request.TokenRefreshRequest;
import com.app.merrbioapi.model.dto.response.AuthenticationResponse;
import com.app.merrbioapi.model.dto.response.RegisterResponse;
import com.app.merrbioapi.model.dto.response.TokenRefreshResponse;
import com.app.merrbioapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful authentication", 
                     content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(summary = "Register new customer", description = "Create new customer account and return JWT tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful customer registration",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email or phone number already exists")
    })
    @PostMapping("/register/customer")
    public ResponseEntity<AuthenticationResponse> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }

    @Operation(summary = "Register new farmer", description = "Create new farmer account and return JWT tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful farmer registration",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email or phone number already exists")
    })
    @PostMapping("/register/farmer")
    public ResponseEntity<AuthenticationResponse> registerFarmer(@Valid @RequestBody FarmerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerFarmer(request));
    }

    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful token refresh", 
                     content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
        @ApiResponse(responseCode = "403", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(summary = "Logout", description = "Invalidate the provided refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful logout"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logout successful");
    }

    @Operation(summary = "Logout from all devices", description = "Invalidate all active refresh tokens for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully logged out from all devices"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            authService.logoutAll(userDetails.getUsername());
            return ResponseEntity.ok("Logged out from all devices");
        }

        return ResponseEntity.badRequest().body("Not authenticated");
    }
}