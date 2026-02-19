package com.spring.qtallisura.controller;

import com.spring.qtallisura.service.CategoriaService;
import com.spring.qtallisura.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/")
    public String index() {
        log.info("HomeController.index() - Renderizando página de inicio");
        return "cliente/index";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        log.info("HomeController.catalogo() - Renderizando catálogo");
        try {
            model.addAttribute("productos", productoService.allList());
            model.addAttribute("categorias", categoriaService.allList());
        } catch (Exception e) {
            log.error("Error al cargar productos: ", e);
        }
        return "cliente/catalogo";
    }

    @GetMapping("/resenas")
    public String resenas() {
        log.info("HomeController.resenas() - Renderizando reseñas");
        return "cliente/resenas";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        log.info("HomeController.nosotros() - Renderizando página nosotros");
        return "cliente/nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        log.info("HomeController.contacto() - Renderizando página de contacto");
        return "cliente/contacto";
    }

    @GetMapping("/reservas")
    public String reservas() {
        log.info("HomeController.reservas() - Renderizando página de reservas");
        return "cliente/reservas";
    }

    @GetMapping("/login")
    public String login() {
        log.info("HomeController.login() - Renderizando página de login");
        return "auth/login";
    }

    @GetMapping("/admin")
    public String admin() {
        log.info("HomeController.admin() - Renderizando página de admin login");
        return "auth/admin";
    }

    @GetMapping("/mesero-login")
    public String meseroLogin() {
        log.info("HomeController.meseroLogin() - Renderizando página de mesero login");
        return "auth/mesero";
    }
}

