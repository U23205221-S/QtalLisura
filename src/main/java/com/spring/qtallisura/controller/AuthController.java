package com.spring.qtallisura.controller;

import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Endpoint para login de usuarios (cliente o admin)
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        log.info("Intento de login para usuario: {}", username);

        try {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar contraseña
            if (!passwordEncoder.matches(password, usuario.getContrasena())) {
                log.warn("Contraseña incorrecta para usuario: {}", username);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Credenciales incorrectas"
                ));
            }

            // Guardar usuario en sesión
            session.setAttribute("usuarioLogueado", usuario);
            log.info("✓ Login exitoso para usuario: {}", username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login exitoso",
                    "perfil", usuario.getIdPerfil().getNombre(),
                    "redirectUrl", determineRedirectUrl(usuario)
            ));

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error en el login: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            log.info("Logout de usuario: {}", usuario.getUsername());
        }
        session.invalidate();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logout exitoso",
                "redirectUrl", "/login"
        ));
    }

    /**
     * Endpoint para verificar sesión activa
     */
    @GetMapping("/check-session")
    @ResponseBody
    public ResponseEntity<?> checkSession(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario != null) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "username", usuario.getUsername(),
                    "perfil", usuario.getIdPerfil().getNombre()
            ));
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    /**
     * Determina la URL de redirección según el perfil del usuario
     */
    private String determineRedirectUrl(Usuario usuario) {
        String perfil = usuario.getIdPerfil().getNombre();

        if ("Administrador".equalsIgnoreCase(perfil)) {
            return "/admin/dashboard";
        }

        return "/catalogo"; // Por defecto para clientes
    }
}

