package com.spring.qtallisura.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Configuracion de sesiones para separar cookies de admin y cliente.
 * Permite tener sesiones independientes en el mismo navegador.
 */
@Configuration
@Slf4j
public class SessionConfig {

    private static final String ADMIN_COOKIE_NAME = "ADMIN_SESSION";
    private static final String CLIENT_COOKIE_NAME = "JSESSIONID";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DualSessionFilter dualSessionFilter() {
        return new DualSessionFilter();
    }

    /**
     * Filtro que maneja cookies de sesion separadas para admin y cliente.
     */
    public static class DualSessionFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        FilterChain filterChain) throws ServletException, IOException {
            
            String path = request.getServletPath();
            boolean isAdminPath = isAdminPath(path);
            String targetCookieName = isAdminPath ? ADMIN_COOKIE_NAME : CLIENT_COOKIE_NAME;

            log.debug("DualSessionFilter - Path: {}, isAdmin: {}, Cookie: {}", path, isAdminPath, targetCookieName);

            // Wrapper que mapea la cookie correcta a JSESSIONID para el contenedor
            HttpServletRequest wrappedRequest = new SessionCookieRequestWrapper(request, targetCookieName);
            
            // Wrapper para interceptar la creacion de cookies de sesion
            SessionCookieResponseWrapper wrappedResponse = new SessionCookieResponseWrapper(response, targetCookieName, isAdminPath);

            filterChain.doFilter(wrappedRequest, wrappedResponse);
        }

        private boolean isAdminPath(String path) {
            return path.startsWith("/admin") || path.startsWith("/auth/admin");
        }
    }

    /**
     * Request wrapper que traduce la cookie personalizada a JSESSIONID.
     */
    private static class SessionCookieRequestWrapper extends HttpServletRequestWrapper {
        
        private final String targetCookieName;

        public SessionCookieRequestWrapper(HttpServletRequest request, String targetCookieName) {
            super(request);
            this.targetCookieName = targetCookieName;
        }

        @Override
        public Cookie[] getCookies() {
            Cookie[] cookies = super.getCookies();
            if (cookies == null) {
                return null;
            }

            // Si estamos buscando ADMIN_SESSION, la mapeamos a JSESSIONID
            return Arrays.stream(cookies)
                    .map(cookie -> {
                        if (targetCookieName.equals(cookie.getName())) {
                            Cookie mappedCookie = new Cookie("JSESSIONID", cookie.getValue());
                            mappedCookie.setPath(cookie.getPath());
                            mappedCookie.setHttpOnly(cookie.isHttpOnly());
                            mappedCookie.setSecure(cookie.getSecure());
                            return mappedCookie;
                        }
                        // Si es JSESSIONID pero estamos en admin, ignorarla
                        if ("JSESSIONID".equals(cookie.getName()) && ADMIN_COOKIE_NAME.equals(targetCookieName)) {
                            return null;
                        }
                        // Si es ADMIN_SESSION pero estamos en cliente, ignorarla
                        if (ADMIN_COOKIE_NAME.equals(cookie.getName()) && CLIENT_COOKIE_NAME.equals(targetCookieName)) {
                            return null;
                        }
                        return cookie;
                    })
                    .filter(c -> c != null)
                    .toArray(Cookie[]::new);
        }

        @Override
        public String getRequestedSessionId() {
            Cookie[] cookies = super.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (targetCookieName.equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Response wrapper que renombra la cookie JSESSIONID segun el contexto.
     */
    private static class SessionCookieResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        
        private final String targetCookieName;
        private final boolean isAdminPath;

        public SessionCookieResponseWrapper(HttpServletResponse response, String targetCookieName, boolean isAdminPath) {
            super(response);
            this.targetCookieName = targetCookieName;
            this.isAdminPath = isAdminPath;
        }

        @Override
        public void addCookie(Cookie cookie) {
            if ("JSESSIONID".equals(cookie.getName())) {
                Cookie customCookie = new Cookie(targetCookieName, cookie.getValue());
                customCookie.setPath(isAdminPath ? "/admin" : "/");
                customCookie.setHttpOnly(true);
                customCookie.setSecure(cookie.getSecure());
                customCookie.setMaxAge(cookie.getMaxAge());
                
                log.debug("Renombrando cookie JSESSIONID a {} para path {}", targetCookieName, customCookie.getPath());
                super.addCookie(customCookie);
            } else {
                super.addCookie(cookie);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if ("Set-Cookie".equalsIgnoreCase(name) && value != null && value.contains("JSESSIONID")) {
                // Reemplazar JSESSIONID por el nombre correcto
                String modifiedValue = value.replace("JSESSIONID", targetCookieName);
                if (isAdminPath && !modifiedValue.contains("Path=/admin")) {
                    modifiedValue = modifiedValue.replaceFirst("Path=/[^;]*", "Path=/admin");
                }
                super.addHeader(name, modifiedValue);
            } else {
                super.addHeader(name, value);
            }
        }

        @Override
        public void setHeader(String name, String value) {
            if ("Set-Cookie".equalsIgnoreCase(name) && value != null && value.contains("JSESSIONID")) {
                String modifiedValue = value.replace("JSESSIONID", targetCookieName);
                if (isAdminPath && !modifiedValue.contains("Path=/admin")) {
                    modifiedValue = modifiedValue.replaceFirst("Path=/[^;]*", "Path=/admin");
                }
                super.setHeader(name, modifiedValue);
            } else {
                super.setHeader(name, value);
            }
        }
    }
}
