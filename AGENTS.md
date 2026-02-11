# AGENTS.md - Q'Tal Lisura Codebase Guidelines

> Instructions for AI coding agents operating in this repository.

## Project Overview

**Q'Tal Lisura** is a Spring Boot 4.x restaurant management system with:
- Backend: Java 21, Spring Boot, Spring Data JPA, Spring Security, Lombok, MapStruct
- Frontend: Thymeleaf templates, vanilla JavaScript, Bootstrap 5
- Database: H2 (dev), MySQL (prod)
- Architecture: MVC Monolithic (Controller -> Service -> Repository).


# Code Style Guidelines (The Gentleman Way)

## Java & Spring Boot
- **Architecture:** Use strictly Layered Architecture. Controllers should NEVER call Repositories directly.
- **Data:** Use DTOs (Records) for data transfer. Never pass `@Entity` to the Thymeleaf view directly if possible.
- **Injection:** Always use Constructor Injection (`private final` fields + `@RequiredArgsConstructor` from Lombok). DO NOT use `@Autowired` on fields.
- **JPA:** Use JPQL or Specifications for complex queries. Avoid raw SQL.

## Frontend (Thymeleaf + Bootstrap)
- **CSS:** Use Bootstrap 5 utility classes (e.g., `d-flex`, `mb-3`) instead of custom CSS whenever possible.
- **JavaScript:** Use Vanilla JS. Do not assume React or Angular implies. Keep scripts simple and modular.
- **Thymeleaf:** Use `th:object` for forms and `th:each` for lists. Ensure fragments are used for Header/Footer to avoid code duplication.
## Build & Run Commands

```bash
# Build project
./mvnw clean install

# Run application (port 8080)
./mvnw spring-boot:run

# Run ALL tests
./mvnw test

# Run a SINGLE test class
./mvnw test -Dtest=QtalLisuraApplicationTests

# Run a SINGLE test method
./mvnw test -Dtest=QtalLisuraApplicationTests#contextLoads

# Skip tests during build
./mvnw clean install -DskipTests
```

## Project Structure

```
src/main/java/com/spring/qtallisura/
├── controller/          # REST controllers (singular: ProductoController)
├── service/             # Business logic (implements ServiceAbs)
│   └── abstractService/ # CRUD interfaces: Creatable, Readable, Updatable, Removable, Listable
├── repository/          # Spring Data JPA repositories
├── model/               # JPA entities (singular: Producto, Usuario)
├── dto/request/         # Input DTOs with validation
├── dto/response/        # Output DTOs
├── mapper/mapperImpl/   # MapStruct implementations
├── config/              # Security, Web, DataLoader
└── exception/           # EServiceLayer, GlobalExceptionHandler
```

## Code Style Guidelines

### Naming Conventions

| Element      | Convention   | Example                      |
|--------------|--------------|------------------------------|
| Entity       | Singular     | `Producto`, `Mesa`           |
| Endpoint     | Singular     | `/producto`, `/mesa`         |
| Variables    | camelCase    | `productoService`            |
| DB Columns   | snake_case   | `precio_venta`, `estado_bd`  |
| Primary Key  | id + Entity  | `idProducto`, `idUsuario`    |

### Entity Pattern

```java
@Entity @Table(name = "Producto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria idCategoria;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;  // Required: ACTIVO, INACTIVO, ELIMINADO
}
```

### Service Pattern

```java
@Service @Slf4j @RequiredArgsConstructor
public class ProductoService implements ServiceAbs<ProductoRequestDTO, ProductoResponseDTO> {
    
    @Transactional @Override
    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        log.info("ProductoService.create()");  // Always log method entry
        // Throw EServiceLayer for business errors
    }
}
```

### Controller Pattern

```java
@RestController @RequestMapping("/producto")  // SINGULAR endpoint
@RequiredArgsConstructor @Slf4j
public class ProductoController {
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> create(...) {
        log.info("Recibida solicitud para crear un nuevo producto");
        return ResponseEntity.status(201).body(service.create(dto));
    }
}
```

### DTO Patterns

**Request:** `@Getter @Setter @Builder` + validation
```java
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
private String nombre;
```

**Response:** `@Data @Builder` with flattened relationship fields

### MapStruct Mapper

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductoMapper extends Convert<Producto, ProductoRequestDTO, ProductoResponseDTO> {
    @Mapping(target = "categoriaNombre", source = "idCategoria.nombre")
    ProductoResponseDTO toDTO(Producto model);
}
```

### Error Handling

- Throw `EServiceLayer` for business errors
- `GlobalExceptionHandler` catches all exceptions
- Response: `{ "success": false, "message": "Error message" }`

### JavaScript (Frontend)

- Vanilla JS with `async/await` + `fetch()`
- Bootstrap 5 components
- Always try/catch with `showNotification()` for errors

```javascript
async function cargarProductos() {
    try {
        const response = await fetch('/producto');
        if (response.ok) { productosData = await response.json(); }
    } catch (error) { showNotification('Error de conexion', 'error'); }
}
```

## API Endpoints

- SINGULAR nouns: `/producto`, `/categoria`, `/mesa`, `/reserva`
- Exception: `/api/auth/*` for authentication
- DELETE = soft delete (sets `estadoBD = ELIMINADO`)

## Key Rules

1. **Soft delete only** - Never hard delete, use `EstadoBD.ELIMINADO`
2. **Lombok everywhere** - `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Slf4j`, `@Builder`
3. **Log method entry** - `log.info("ClassName.methodName()")`
4. **Validation in DTOs** - `@NotBlank`, `@NotNull`, `@Size`, `@Valid`
5. **FetchType.LAZY** - Always for relationships
6. **No Magic Numbers/Strings** - Use Enums or Constants for strict values (like `EstadoBD`).
7. **Early Return** - Avoid nested `if/else`. Return fast inside validations.
8. **Spanish Logic, English Code** - Variable names and comments can be Spanish (as per current code), but keywords strictly English.

## URLs

- Public: `http://localhost:8080/`, `/catalogo`, `/reservas`
- Admin: `/auth/admin`, `/admin/dashboard`
- H2 Console: `/h2-console` (dev only)
- Default: `admin` / `admin123`
