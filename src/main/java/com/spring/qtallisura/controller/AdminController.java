package com.spring.qtallisura.controller;

import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.repository.ProductoRepository;
import com.spring.qtallisura.repository.ResenaRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ResenaRepository resenaRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        log.info("AdminController.dashboard()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        // Agregar estadísticas básicas al modelo
        long totalProductos = productoRepository.count();
        long totalPedidos = pedidoRepository.count();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("currentPath", "/admin/dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String productos(Model model, HttpSession session) {
        log.info("AdminController.productos()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        return "admin/productos";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model, HttpSession session) {
        log.info("AdminController.usuarios()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        return "admin/usuarios";
    }

    @GetMapping("/categorias")
    public String categorias(Model model, HttpSession session) {
        log.info("AdminController.categorias()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        return "admin/categorias";
    }

    @GetMapping("/mesas")
    public String mesas(Model model, HttpSession session) {
        log.info("AdminController.mesas()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        return "admin/mesas";
    }

    @GetMapping("/reservas")
    public String reservas(Model model, HttpSession session) {
        log.info("AdminController.reservas()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("currentPath", "/admin/reservas");

        return "admin/reservas";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, HttpSession session) {
        log.info("AdminController.pedidos()");

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("currentPath", "/admin/pedidos");

        return "admin/pedidos";
    }
}
