# AGENTS.md - Q'Tal Lisura Restaurant Management System

## Project Overview

Spring Boot 4.0.1 monolith application for restaurant management with Thymeleaf frontend.
- **Java**: 21
- **Database**: MySQL 8 (Docker container)
- **Frontend**: Thymeleaf templates with vanilla JS
- **Build Tool**: Maven

## Build & Run Commands

```bash
# Start application (requires MySQL running)
./mvnw spring-boot:run

# Build without tests
./mvnw clean package -DskipTests

# Build with tests
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=QtalLisuraApplicationTests

# Run a single test method
./mvnw test -Dtest=QtalLisuraApplicationTests#contextLoads

# Run tests matching pattern
./mvnw test -Dtest="*Service*"

# Clean build
./mvnw clean
```

## Database Setup

MySQL runs on Docker. Connection in `application.properties`:
```
jdbc:mysql://localhost:3306/dbrestaurant
username: root
password: root
```

Hibernate DDL mode is `update` - schema auto-updates.

---

## Project Structure

```
src/main/java/com/spring/qtallisura/
├── config/           # Security, Web, Session configs
├── controller/       # REST + Web controllers
├── dto/
│   ├── request/      # Input DTOs with validation
│   └── response/     # Output DTOs (read-only)
├── exception/        # EServiceLayer, GlobalExceptionHandler
├── mapper/
│   ├── convert/      # Mapper interfaces
│   └── mapperImpl/   # MapStruct implementations
├── model/            # JPA entities
├── repository/       # Spring Data JPA repos
├── service/          # Business logic
│   └── abstractService/  # CRUD interfaces
└── utility/          # Validation groups, helpers

src/main/resources/
├── templates/        # Thymeleaf views
│   ├── admin/        # Admin panel views
│   ├── auth/         # Login pages
│   ├── cliente/      # Public client views
│   ├── fragments/    # Reusable HTML fragments
│   └── mesero/       # Waiter panel views
├── static/
│   ├── css/
│   ├── js/
│   └── images/
└── application.properties
```

---

## Code Style Guidelines

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Classes | PascalCase | `PedidoService`, `ClienteController` |
| Methods | camelCase | `findByUsuario()`, `validarTransicionEstado()` |
| Variables | camelCase | `estadoPedido`, `clienteLogueado` |
| Constants | SCREAMING_SNAKE | `PENDIENTE`, `EN_PREPARACION` |
| Packages | lowercase | `com.spring.qtallisura.service` |
| DTOs | Suffix `RequestDTO`/`ResponseDTO` | `PedidoRequestDTO` |
| Mappers | Suffix `Mapper` | `PedidoMapper` |

### Entity Conventions

```java
@Entity
@Table(name = "Pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;  // Always Integer, prefixed with 'id'
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente idCliente;  // FK named idXxx matching column
    
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoPedido;  // Enums as STRING in DB
    
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;  // Soft delete: ACTIVO/INACTIVO/ELIMINADO
}
```

### DTO Conventions

**Request DTOs** - Include validation annotations:
```java
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PedidoRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    private Integer idUsuario;
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 6)
    private String codigo;
}
```

**Response DTOs** - Simple read-only data:
```java
@Data @Builder
public class PedidoResponseDTO {
    private Integer idPedido;
    private String clienteNombre;  // Flattened from relations
    private String estadoPedido;   // Human-readable enum value
}
```

### Service Layer

- Implement `ServiceAbs<RequestDTO, ResponseDTO>` for CRUD operations
- Use `@Transactional` on methods
- Use `@Slf4j` for logging
- Throw `EServiceLayer` for business errors
- Soft delete pattern: set `estadoBD = EstadoBD.ELIMINADO`

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService implements ServiceAbs<PedidoRequestDTO, PedidoResponseDTO> {
    
    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    
    @Transactional
    @Override
    public PedidoResponseDTO create(PedidoRequestDTO dto) {
        log.info("PedidoService.create()");
        // ... validation logic
        throw new EServiceLayer("El código del pedido ya está registrado");
    }
}
```

### Controller Conventions

**REST Controllers** - For API endpoints:
```java
@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {
    
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Web Controllers** - For Thymeleaf views:
```java
@Controller
public class HomeController {
    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("productos", productoService.allList());
        return "cliente/catalogo";  // templates/cliente/catalogo.html
    }
}
```

### MapStruct Mappers

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoMapper extends Convert<Pedido, PedidoRequestDTO, PedidoResponseDTO> {
    
    @Mapping(target = "clienteNombre", expression = "java(...)")
    @Mapping(target = "estadoPedido", source = "estadoPedido.estado")
    PedidoResponseDTO toDTO(Pedido model);
    
    @Mapping(target = "idCliente", ignore = true)  // Set in service
    Pedido toModel(PedidoRequestDTO dto);
}
```

### Error Handling

Global exception handler returns consistent JSON:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EServiceLayer.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleServiceException(EServiceLayer ex) {
        return Map.of("success", false, "message", ex.getMessage());
    }
}
```

### Authentication Pattern

Session-based auth with manual checks (Spring Security disabled for dev):
```java
Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
if (cliente == null) {
    return "redirect:/login";
}
```

Session keys: `clienteLogueado`, `usuarioLogueado`, `idUsuario`, `perfilUsuario`

---

## Business Rules

### Order State Machine
```
PENDIENTE → EN_PREPARACION → SERVIDO → PAGADO
     ↓              ↓            ↓
  CANCELADO    CANCELADO    CANCELADO
```
- PAGADO and CANCELADO are terminal states
- Cannot change state of PAGADO/CANCELADO orders
- Cancellation frees the table

---

## Import Order

1. java.* / jakarta.*
2. External libraries (org.*, com.*)
3. Project imports (com.spring.qtallisura.*)

Use Lombok: `@Getter @Setter @RequiredArgsConstructor @Slf4j @Builder`

---

## Testing

- Test classes in `src/test/java/` mirroring main structure
- Use `@SpringBootTest` for integration tests
- Naming: `*Tests.java` suffix

```bash
# Run specific test
./mvnw test -Dtest=PedidoServiceTest#shouldCreatePedido
```
