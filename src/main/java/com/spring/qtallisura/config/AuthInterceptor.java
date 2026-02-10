package com.spring.qtallisura.config;

import com.spring.qtallisura.model.Modulo;
import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.ModuloRepository;
import com.spring.qtallisura.repository.PerfilModuloRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final PerfilModuloRepository perfilModuloRepository;
    private final ModuloRepository moduloRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Rutas públicas que no requieren autenticación
        if (isPublicPath(requestURI)) {
            log.debug("Ruta pública: {}", requestURI);
            return true;
        }

        // Verificar si hay sesión activa
        if (session == null) {
            log.warn("Acceso no autorizado a: {} - Redirigiendo a login", requestURI);
            response.sendRedirect("/login");
            return false;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Object cliente = session.getAttribute("clienteLogueado");

        if (isAdminPath(requestURI)) {
            if (usuario == null) {
                log.warn("Acceso no autorizado a admin: {} - Redirigiendo a login", requestURI);
                response.sendRedirect("/login");
                return false;
            }
            return true;
        }

        if (usuario == null && cliente == null) {
            log.warn("Acceso no autorizado a: {} - Redirigiendo a login", requestURI);
            response.sendRedirect("/login");
            return false;
        }

        log.debug("Usuario autenticado accediendo a: {}", requestURI);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Inyectar datos globales en el modelo solo si hay un ModelAndView
        if (modelAndView != null) {
            // Siempre inyectar la ruta actual para uso en templates
            String currentPath = request.getRequestURI();
            modelAndView.addObject("currentPath", currentPath);

            // Solo inyectar usuario y módulos si NO es ruta pública
            if (!isPublicPath(currentPath)) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

                    if (usuario != null) {
                        // Obtener módulos permitidos para el usuario
                        List<Modulo> modulosPermitidos = getModulosPermitidos(usuario);

                        // Agregar al modelo
                        modelAndView.addObject("usuarioLogueado", usuario);
                        modelAndView.addObject("modulosPermitidos", modulosPermitidos);

                        log.debug("Módulos inyectados para usuario {}: {}",
                                usuario.getUsername(),
                                modulosPermitidos.stream().map(Modulo::getNombre).collect(Collectors.toList()));
                    }
                }
            }
        }
    }

    /**
     * Obtiene los módulos permitidos para un usuario según su perfil
     */
    private List<Modulo> getModulosPermitidos(Usuario usuario) {
        if (usuario == null || usuario.getIdPerfil() == null) {
            return List.of();
        }

        return perfilModuloRepository.findAll().stream()
                .filter(pm -> pm.getIdPerfil().getIdPerfil().equals(usuario.getIdPerfil().getIdPerfil()))
                .map(pm -> pm.getIdModulo())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Determina si una ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/admin") ||
               path.equals("/catalogo") ||
               path.equals("/resenas") ||
               path.equals("/nosotros") ||
               path.startsWith("/api/auth") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/uploads/") ||
               path.startsWith("/h2-console") ||
               path.contains("favicon.ico");
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/admin");
    }
}
