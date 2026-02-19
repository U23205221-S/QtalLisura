package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ClienteRequestDTO;
import com.spring.qtallisura.dto.response.ClienteResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.model.Cliente;
import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.ClienteRepository;
import com.spring.qtallisura.repository.UsuarioRepository;
import com.spring.qtallisura.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
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
            // Primero intentar buscar como Usuario (admin/personal)
            Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

            if (usuario != null) {
                // Es un usuario del sistema
                if (!passwordEncoder.matches(password, usuario.getContrasena())) {
                    log.warn("Contraseña incorrecta para usuario: {}", username);
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Credenciales incorrectas"
                    ));
                }

                // Guardar usuario en sesión
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("idUsuario", usuario.getIdUsuario());
                session.setAttribute("nombreUsuario", usuario.getNombres() + " " + usuario.getApellidos());
                session.setAttribute("perfilUsuario", usuario.getIdPerfil().getNombre());
                log.info("✓ Login exitoso para usuario: {}", username);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Login exitoso",
                        "idUsuario", usuario.getIdUsuario(),
                        "nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos(),
                        "perfil", usuario.getIdPerfil().getNombre(),
                        "redirectUrl", determineRedirectUrl(usuario)
                ));
            }

            // Si no es usuario, intentar buscar como Cliente (por DNI)
            Cliente cliente = clienteRepository.findByDNI(username).orElse(null);

            if (cliente != null) {
                // Es un cliente
                if (!passwordEncoder.matches(password, cliente.getContrasena())) {
                    log.warn("Contraseña incorrecta para cliente con DNI: {}", username);
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Credenciales incorrectas"
                    ));
                }

                // Guardar cliente en sesión
                session.setAttribute("clienteLogueado", cliente);
                session.setAttribute("idCliente", cliente.getIdCliente());
                session.setAttribute("nombreCliente", cliente.getNombre() + " " + cliente.getApellido());
                // Compatibilidad con templates que usan clienteId/clienteNombre
                session.setAttribute("clienteId", cliente.getIdCliente());
                session.setAttribute("clienteNombre", cliente.getNombre() + " " + cliente.getApellido());
                log.info("✓ Login exitoso para cliente: {}", cliente.getNombre());

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Login exitoso",
                        "idCliente", cliente.getIdCliente(),
                        "nombreCompleto", cliente.getNombre() + " " + cliente.getApellido(),
                        "perfil", "Cliente",
                        "redirectUrl", "/catalogo"
                ));
            }

            // No se encontró ni usuario ni cliente
            log.warn("No se encontró usuario o cliente con identificador: {}", username);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Credenciales incorrectas"
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
     * Endpoint para registro de clientes (público)
     */
    @PostMapping("/registro")
    @ResponseBody
    public ResponseEntity<?> registroCliente(@Valid @RequestBody ClienteRequestDTO clienteRequest) {
        log.info("Recibida solicitud de registro de cliente con DNI: {}", clienteRequest.getDNI());

        try {
            // Crear cliente usando el servicio
            ClienteResponseDTO clienteCreado = clienteService.create(clienteRequest);

            log.info("✓ Cliente registrado exitosamente con ID: {}", clienteCreado.getIdCliente());

            return ResponseEntity.status(201).body(Map.of(
                    "success", true,
                    "message", "¡Registro exitoso! Ya puedes iniciar sesión",
                    "cliente", clienteCreado
            ));

        } catch (EServiceLayer e) {
            log.error("Error de negocio en registro de cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error inesperado en registro de cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error inesperado: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        String redirectUrl = "/";

        // Verificar si es un usuario del sistema
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            log.info("Logout de usuario: {}", usuario.getUsername());
            String perfil = usuario.getIdPerfil().getNombre();
            if ("Mesero".equalsIgnoreCase(perfil)) {
                redirectUrl = "/mesero-login";
            } else {
                redirectUrl = "/admin"; // Redirigir a la página de login de admin
            }
        }

        // Verificar si es un cliente
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente != null) {
            log.info("Logout de cliente: {} {}", cliente.getNombre(), cliente.getApellido());
            redirectUrl = "/"; // Redirigir a la página principal para clientes
        }

        // Invalidar la sesión completa
        session.invalidate();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sesión cerrada exitosamente",
                "redirectUrl", redirectUrl
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
                    "idUsuario", usuario.getIdUsuario(),
                    "username", usuario.getUsername(),
                    "nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos(),
                    "perfil", usuario.getIdPerfil().getNombre()
            ));
        }

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente != null) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "idCliente", cliente.getIdCliente(),
                    "nombreCompleto", cliente.getNombre() + " " + cliente.getApellido(),
                    "perfil", "Cliente"
            ));
        }

        return ResponseEntity.ok(Map.of("authenticated", false));
    }

    /**
     * Endpoint para obtener el usuario actual de la sesión
     */
    @GetMapping("/current-user")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "No hay sesión activa"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "idUsuario", usuario.getIdUsuario(),
                "username", usuario.getUsername(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos(),
                "perfil", usuario.getIdPerfil().getNombre()
        ));
    }

    /**
     * Determina la URL de redirección según el perfil del usuario
     */
    private String determineRedirectUrl(Usuario usuario) {
        String perfil = usuario.getIdPerfil().getNombre();

        if ("Administrador".equalsIgnoreCase(perfil)) {
            return "/admin/dashboard";
        }

        if ("Mesero".equalsIgnoreCase(perfil)) {
            return "/mesero/dashboard";
        }

        return "/catalogo"; // Por defecto para clientes
    }
}

