package com.spring.qtallisura.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${file.upload-dir.users:uploads/users}")
    private String userUploadDir;

    @Value("${file.upload-dir.products:uploads/products}")
    private String productUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir imágenes de usuarios
        String userPath = Paths.get(userUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/users/**")
                .addResourceLocations(userPath);

        // Servir imágenes de productos
        String productPath = Paths.get(productUploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(productPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/h2-console/**",
                        "/api/auth/**"
                );
    }
}

