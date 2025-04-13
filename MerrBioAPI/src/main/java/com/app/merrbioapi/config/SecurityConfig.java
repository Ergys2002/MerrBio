package com.app.merrbioapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;

    // Updated Swagger UI paths with more specific patterns for Spring Boot 3
    private static final String[] PUBLIC_PATHS_GENERAL = {
            "/auth/**", // Simpler pattern for all auth endpoints
            // Swagger UI and OpenAPI paths
            "/v3/api-docs/**",
            "/api/v1/v3/api-docs/**", // Keep if prefix is used
            "/swagger-ui/**",
            "/api/v1/swagger-ui/**", // Keep if prefix is used
            "/swagger-ui.html",
            "/api/v1/swagger-ui.html", // Keep if prefix is used
            "/swagger-resources/**",
            "/api/v1/swagger-resources/**", // Keep if prefix is used
            "/webjars/**",
            "/api/v1/webjars/**", // Keep if prefix is used
            "/error",
            "/api/v1/error", // Keep if prefix is used
            "/img/**",
            "/products/advanced-search",
            
            // WebSocket endpoints
            "/ws/**",
            "/api/v1/ws/**",
            "/topic/**",
            "/api/v1/topic/**",
            "/queue/**",
            "/api/v1/queue/**",
            
            // Chat documentation endpoints for Swagger
            "/chat-socket-docs/**",
            "/api/v1/chat-socket-docs/**"
    };

    // Define paths for specific HTTP methods if needed

    private static final String[] PUBLIC_PATHS_GET_ONLY = {
            "/categories",             // Keep existing ones
            "/api/v1/categories",      // Keep existing ones

            // Add the farmers endpoint path here
            "/farmers",                // If no prefix is used
            "/api/v1/farmers",         // If /api/v1 prefix is used
            
            // Add chat endpoints for documentation in Swagger
            "/chat/**",                // If no prefix is used 
            "/api/v1/chat/**"          // If /api/v1 prefix is used

            // Add other public GET endpoints like /products, /products/{id} etc. if needed
    };


    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS_GENERAL).permitAll() // Permit general public paths for any method
                        .requestMatchers(HttpMethod.GET, PUBLIC_PATHS_GET_ONLY).permitAll() // Permit specific GET paths
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .userDetailsService(userDetailsService)
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}