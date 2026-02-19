package com.spring.qtallisura.controller;

import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Pedido;
import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mesero")
@RequiredArgsConstructor
@Slf4j
public class MeseroController {

    private final PedidoRepository pedidoRepository;

    private Usuario getUsuarioMesero(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return null;
        }
        String perfil = usuario.getIdPerfil().getNombre();
        if (!"Mesero".equalsIgnoreCase(perfil)) {
            return null;
        }
        return usuario;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        log.info("MeseroController.dashboard()");

        Usuario usuario = getUsuarioMesero(session);
        if (usuario == null) {
            return "redirect:/mesero-login";
        }

        // Estadísticas de pedidos del mesero
        List<Pedido> misPedidos = pedidoRepository.findByIdUsuario_IdUsuario(usuario.getIdUsuario())
                .stream().filter(p -> p.getEstadoBD() != EstadoBD.ELIMINADO).toList();

        long pendientes = misPedidos.stream().filter(p -> p.getEstadoPedido() == Pedido.EstadoPedido.PENDIENTE).count();
        long enPreparacion = misPedidos.stream().filter(p -> p.getEstadoPedido() == Pedido.EstadoPedido.EN_PREPARACION).count();
        long servidos = misPedidos.stream().filter(p -> p.getEstadoPedido() == Pedido.EstadoPedido.SERVIDO).count();
        long pagados = misPedidos.stream().filter(p -> p.getEstadoPedido() == Pedido.EstadoPedido.PAGADO).count();

        model.addAttribute("totalPedidos", misPedidos.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enPreparacion", enPreparacion);
        model.addAttribute("servidos", servidos);
        model.addAttribute("pagados", pagados);
        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("currentPath", "/mesero/dashboard");

        return "mesero/dashboard";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, HttpSession session) {
        log.info("MeseroController.pedidos()");

        Usuario usuario = getUsuarioMesero(session);
        if (usuario == null) {
            return "redirect:/mesero-login";
        }

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("currentPath", "/mesero/pedidos");

        return "mesero/pedidos";
    }
}

