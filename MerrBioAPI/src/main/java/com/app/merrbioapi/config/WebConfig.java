package com.app.merrbioapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:${user.dir}/src/main/resources/static/img}")
    private String uploadDir;

    @Value("${app.upload.base-url:/img}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This maps your external folder to the /img URL path
        registry.addResourceHandler(baseUrl + "/**")
                .addResourceLocations("file:" + uploadDir + "/");

        // Add standard resource locations if needed
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}