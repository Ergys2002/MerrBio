package com.app.merrbioapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MerrBio API")
                        .description("API for MerrBio application - A platform for organic farmers and customers")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MerrBio Team")
                                .email("info@merrbio.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://merrbio.com/license")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi publicApi() {

        String[] pathsToInclude = {
                "/auth/**",       // Context path + /auth/**
                "/products/**",   // Context path + /products/**
                "/categories/**", // Context path + /categories/**
                "/farmers/**",    // Context path + /farmers/**
                "/users/**"
        };

        return GroupedOpenApi.builder()
                .group("merrbio-api")
                .pathsToMatch(pathsToInclude)
                .build();
    }

}