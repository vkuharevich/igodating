package com.igodating.questionary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins("*") // Allow all origins
                .allowedMethods("*") // Allow all methods (GET, POST, PUT, DELETE, etc.)
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(false) // Set to true if you need to support cookies/auth
                .maxAge(3600);
    }
}