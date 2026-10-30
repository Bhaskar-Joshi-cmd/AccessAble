package com.accessable.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS configuration for the AccessAble backend.
 *
 * Allowed frontend origins are read from the {@code app.frontend-origin}
 * property, which is populated through the {@code FRONTEND_ORIGIN}
 * environment variable. Multiple origins may be supplied as a comma-separated
 * list (whitespace around each origin is trimmed).
 *
 * Only explicitly configured origins are permitted — no wildcards.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.frontend-origin:http://localhost:8001,http://127.0.0.1:8001}")
    private String frontendOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = Arrays.stream(frontendOrigin.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PATCH", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Admin-Key")
                .allowCredentials(false)
                .maxAge(3600);
    }
}