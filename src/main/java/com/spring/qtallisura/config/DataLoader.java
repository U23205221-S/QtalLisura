package com.spring.qtallisura.config;

import com.spring.qtallisura.model.*;
import com.spring.qtallisura.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ModuloRepository moduloRepository;
    private final PerfilRepository perfilRepository;
    private final PerfilModuloRepository perfilModuloRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== Iniciando carga de datos iniciales ===");

        // 1. Crear Módulos
        Modulo moduloDashboard = createModuloIfNotExists("Dashboard", "Panel de control principal con estadísticas");
        Modulo moduloProductos = createModuloIfNotExists("Productos", "Gestión de productos y catálogo");
        Modulo moduloPedidos = createModuloIfNotExists("Pedidos", "Gestión de pedidos del restaurante");

        // 2. Crear Perfiles
        Perfil perfilAdmin = createPerfilIfNotExists("Administrador", "Perfil con acceso completo al sistema");
        Perfil perfilMesero = createPerfilIfNotExists("Mesero", "Perfil para meseros del restaurante");

        // 3. Asignar Módulos al Perfil Administrador
        assignModuleToProfile(perfilAdmin, moduloDashboard);
        assignModuleToProfile(perfilAdmin, moduloProductos);
        assignModuleToProfile(perfilAdmin, moduloPedidos);

        // 3.1 Asignar Módulos al Perfil Mesero
        assignModuleToProfile(perfilMesero, moduloDashboard);
        assignModuleToProfile(perfilMesero, moduloPedidos);

        // 4. Crear Usuarios
        createAdminUserIfNotExists(perfilAdmin);
        createMeseroUserIfNotExists(perfilMesero);

        // 5. Crear Categorías
        Categoria categoriaEntradas = createCategoriaIfNotExists("Entradas", "Platos de entrada y aperitivos");
        Categoria categoriaPlatosFuertes = createCategoriaIfNotExists("Platos Fuertes", "Platos principales");
        Categoria categoriaPostres = createCategoriaIfNotExists("Postres", "Dulces y postres");
        Categoria categoriaBebidas = createCategoriaIfNotExists("Bebidas", "Bebidas frías y calientes");

        // 6. Crear Productos de Ejemplo
        createProductoIfNotExists("Ceviche Mixto", "Ceviche fresco con mariscos seleccionados",
                categoriaEntradas, 35.00, 25.00, "ceviche.jpg", 50, 10);

        createProductoIfNotExists("Lomo Saltado", "Lomo de res salteado con papas fritas",
                categoriaPlatosFuertes, 42.00, 30.00, "lomo-saltado.jpg", 30, 5);

        createProductoIfNotExists("Ají de Gallina", "Crema de ají amarillo con pollo deshilachado",
                categoriaPlatosFuertes, 38.00, 28.00, "aji-gallina.jpg", 25, 5);

        createProductoIfNotExists("Suspiro Limeño", "Postre tradicional peruano con merengue",
                categoriaPostres, 18.00, 12.00, "suspiro.jpg", 40, 10);

        createProductoIfNotExists("Chicha Morada", "Bebida refrescante de maíz morado",
                categoriaBebidas, 8.00, 5.00, "chicha.jpg", 100, 20);

        createProductoIfNotExists("Pisco Sour", "Cóctel peruano a base de pisco",
                categoriaBebidas, 25.00, 15.00, "pisco-sour.jpg", 60, 15);

        log.info("=== Carga de datos iniciales completada ===");
    }

    private Modulo createModuloIfNotExists(String nombre, String descripcion) {
        return moduloRepository.findByNombre(nombre).orElseGet(() -> {
            Modulo modulo = Modulo.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .build();
            modulo = moduloRepository.save(modulo);
            log.info("✓ Módulo creado: {}", nombre);
            return modulo;
        });
    }

    private Perfil createPerfilIfNotExists(String nombre, String descripcion) {
        return perfilRepository.findByNombre(nombre).orElseGet(() -> {
            Perfil perfil = Perfil.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .build();
            perfil = perfilRepository.save(perfil);
            log.info("✓ Perfil creado: {}", nombre);
            return perfil;
        });
    }

    private void assignModuleToProfile(Perfil perfil, Modulo modulo) {
        // Verificar si ya existe la asignación
        boolean exists = perfilModuloRepository.findAll().stream()
                .anyMatch(pm -> pm.getIdPerfil().getIdPerfil().equals(perfil.getIdPerfil())
                        && pm.getIdModulo().getIdModulo().equals(modulo.getIdModulo()));

        if (!exists) {
            PerfilModulo perfilModulo = PerfilModulo.builder()
                    .idPerfil(perfil)
                    .idModulo(modulo)
                    .build();
            perfilModuloRepository.save(perfilModulo);
            log.info("✓ Módulo '{}' asignado al perfil '{}'", modulo.getNombre(), perfil.getNombre());
        }
    }

    private void createAdminUserIfNotExists(Perfil perfil) {
        usuarioRepository.findByUsername("admin").orElseGet(() -> {
            Usuario admin = Usuario.builder()
                    .nombres("Administrador")
                    .apellidos("Sistema")
                    .DNI("12345678")
                    .username("admin")
                    .contrasena(passwordEncoder.encode("123456"))
                    .idPerfil(perfil)
                    .fechaRegistro(LocalDateTime.now())
                    .imagenUrl("default-admin.jpg")
                    .estadoBD(EstadoBD.ACTIVO)
                    .build();
            admin = usuarioRepository.save(admin);
            log.info("✓ Usuario administrador creado - Username: admin, Password: 123456");
            return admin;
        });
    }

    private void createMeseroUserIfNotExists(Perfil perfil) {
        usuarioRepository.findByUsername("mesero").orElseGet(() -> {
            Usuario mesero = Usuario.builder()
                    .nombres("Mesero")
                    .apellidos("Sistema")
                    .DNI("87654321")
                    .username("mesero")
                    .contrasena(passwordEncoder.encode("123456"))
                    .idPerfil(perfil)
                    .fechaRegistro(LocalDateTime.now())
                    .imagenUrl("default-admin.jpg")
                    .estadoBD(EstadoBD.ACTIVO)
                    .build();
            mesero = usuarioRepository.save(mesero);
            log.info("✓ Usuario mesero creado - Username: mesero, Password: 123456");
            return mesero;
        });
    }

    private Categoria createCategoriaIfNotExists(String nombre, String descripcion) {
        if (categoriaRepository.existsByNombre(nombre)) {
            return categoriaRepository.findAll().stream()
                    .filter(c -> c.getNombre().equals(nombre))
                    .findFirst()
                    .orElseThrow();
        }

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .estadoBD(EstadoBD.ACTIVO)
                .build();
        categoria = categoriaRepository.save(categoria);
        log.info("✓ Categoría creada: {}", nombre);
        return categoria;
    }

    private void createProductoIfNotExists(String nombre, String descripcion, Categoria categoria,
                                          Double precioVenta, Double costoUnitario, String imagenUrl,
                                          Integer stockActual, Integer stockMinimo) {
        if (!productoRepository.existsByNombre(nombre)) {
            Producto producto = Producto.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .idCategoria(categoria)
                    .precioVenta(precioVenta)
                    .costoUnitario(costoUnitario)
                    .imagenUrl(imagenUrl)
                    .stockActual(stockActual)
                    .stockMinimo(stockMinimo)
                    .estadoBD(EstadoBD.ACTIVO)
                    .build();
            productoRepository.save(producto);
            log.info("✓ Producto creado: {} - S/ {}", nombre, precioVenta);
        }
    }
}

