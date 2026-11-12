package com.waycation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spring Web MVC configuration for static and runtime-uploaded assets.
 * Maps HTTP requests under {@code /uploads/**} directly to the local filesystem
 * {@code uploads/} directory so hotel owners' uploaded featured images and photo
 * galleries are served immediately without requiring a server restart.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve the local uploads directory to an absolute file:// URI
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadPath = uploadDir.toUri().toString();
        if (!uploadPath.endsWith("/")) {
            uploadPath = uploadPath + "/";
        }

        // Expose /uploads/** for serving uploaded accommodation images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
