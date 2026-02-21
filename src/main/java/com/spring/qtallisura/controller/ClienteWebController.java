package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ResenaRequestDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.model.Cliente;
import com.spring.qtallisura.service.ResenaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador web para las páginas del panel de cliente autenticado
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ClienteWebController {

    private final ResenaService resenaService;

    // ─── Vista de Mis Reseñas ────────────────────────────────────────────────

    @GetMapping("/mis-resenas")
    public String misResenas(Model model, HttpSession session) {
        log.info("ClienteWebController.misResenas()");

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) return "redirect:/login";

        try {
            model.addAttribute("clienteNombre", cliente.getNombre() + " " + cliente.getApellido());
            model.addAttribute("clienteLogueado", cliente);
            model.addAttribute("resenas", resenaService.findByCliente(cliente.getIdCliente()));
        } catch (Exception e) {
            log.error("Error al cargar reseñas del cliente {}: ", cliente.getIdCliente(), e);
            model.addAttribute("resenas", java.util.Collections.emptyList());
        }

        return "cliente/resenas";
    }

    // ─── Buscar pedido por código (AJAX) ────────────────────────────────────

    @GetMapping("/mis-resenas/buscar-pedido")
    @ResponseBody
    public ResponseEntity<?> buscarPedido(@RequestParam String codigo, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No estás autenticado"));
        }

        try {
            ResenaService.PedidoResenaDTO pedido =
                    resenaService.buscarPedidoPorCodigo(codigo, cliente.getIdCliente());
            return ResponseEntity.ok(pedido);
        } catch (EServiceLayer e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al buscar pedido por código: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error inesperado"));
        }
    }

    // ─── Enviar reseña (AJAX) ───────────────────────────────────────────────

    @PostMapping("/mis-resenas/enviar")
    @ResponseBody
    public ResponseEntity<?> enviarResena(@RequestBody Map<String, Object> body, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No estás autenticado"));
        }

        try {
            Integer idPedido    = (Integer) body.get("idPedido");
            Integer idProducto  = (Integer) body.get("idProducto");
            Integer calificacion = (Integer) body.get("calificacion");
            String  comentario  = (String)  body.get("comentario");

            if (idPedido == null || idProducto == null || calificacion == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Datos incompletos"));
            }

            ResenaRequestDTO dto = ResenaRequestDTO.builder()
                    .idCliente(cliente.getIdCliente())
                    .idPedido(idPedido)
                    .idProducto(idProducto)
                    .calificacion(calificacion)
                    .comentario(comentario)
                    .fechaResena(LocalDateTime.now())
                    .build();

            resenaService.create(dto);
            return ResponseEntity.ok(Map.of("message", "Reseña enviada exitosamente"));
        } catch (EServiceLayer e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al enviar reseña: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error inesperado"));
        }
    }
}
