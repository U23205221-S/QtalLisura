package com.spring.qtallisura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - autenticación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/login", "/registro").permitAll()

                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                // Páginas públicas del cliente
                .requestMatchers("/", "/catalogo", "/nosotros", "/contacto", "/resenas").permitAll()

                // Cualquier otra solicitud requiere autenticación
                .anyRequest().permitAll() // Temporalmente permitir todo para desarrollo
            )
            .formLogin(AbstractHttpConfigurer::disable) // Deshabilitar formulario de login por defecto
            .httpBasic(AbstractHttpConfigurer::disable); // Deshabilitar autenticación básica

        return http.build();
    }
}
