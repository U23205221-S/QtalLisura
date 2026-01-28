package com.spring.qtallisura.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir.users:uploads/users}")
    private String userUploadDir;

    @Value("${file.upload-dir.products:uploads/products}")
    private String productUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar el acceso a las imágenes de usuarios
        registry.addResourceHandler("/uploads/users/**")
                .addResourceLocations("file:" + userUploadDir + "/");

        // Configurar el acceso a las imágenes de productos
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations("file:" + productUploadDir + "/");
    }
}

