# 🍕 GastroTech - Sistema de Gestión para Restaurantes

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?style=for-the-badge&logo=thymeleaf)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?style=for-the-badge&logo=bootstrap)
![H2 Database](https://img.shields.io/badge/H2-Database-blue?style=for-the-badge&logo=databricks)

**Sistema integral de gestión para restaurantes con panel administrativo y catálogo para clientes**

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Backend](#-backend)
- [Frontend](#-frontend)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Endpoints API REST](#-endpoints-api-rest)
- [Base de Datos](#-base-de-datos)
- [Capturas de Pantalla](#-capturas-de-pantalla)

---

## 📖 Descripción

**GastroTech** es una aplicación web completa para la gestión de restaurantes, desarrollada con **Spring Boot 4.0.1** en el backend y **Thymeleaf + Bootstrap 5** en el frontend. El sistema permite administrar productos, categorías, usuarios, pedidos, mesas, reservas, pagos y reseñas, además de ofrecer un catálogo atractivo para los clientes.

### ✨ Características Principales

- 🔐 Sistema de autenticación y autorización
- 📦 Gestión completa de productos y categorías (CRUD)
- 👥 Administración de usuarios y perfiles
- 🍽️ Control de mesas y reservas
- 📋 Gestión de pedidos y detalles
- 💳 Registro de pagos
- ⭐ Sistema de reseñas
- 📊 Dashboard administrativo con estadísticas
- 📱 Diseño responsivo

---

## 🏗 Arquitectura del Proyecto

```
rest_gastrotech/
├── src/
│   ├── main/
│   │   ├── java/com/spring/rest_gastrotech/
│   │   │   ├── config/              # Configuraciones (Security, Web, etc.)
│   │   │   ├── controller/          # Controladores REST y MVC
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/         # DTOs de entrada
│   │   │   │   └── response/        # DTOs de salida
│   │   │   ├── exception/           # Excepciones personalizadas
│   │   │   ├── mapper/              # Mappers Entity-DTO (MapStruct)
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   ├── service/             # Lógica de negocio
│   │   │   └── utility/             # Utilidades
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/             # Estilos CSS
│   │       │   └── js/              # Scripts JavaScript
│   │       ├── templates/           # Plantillas Thymeleaf
│   │       │   ├── admin/           # Vistas administrativas
│   │       │   ├── auth/            # Vistas de autenticación
│   │       │   ├── cliente/         # Vistas para clientes
│   │       │   └── fragments/       # Fragmentos reutilizables
│   │       └── application.properties
│   └── test/                        # Tests unitarios
├── uploads/                         # Archivos subidos
│   ├── products/                    # Imágenes de productos
│   └── users/                       # Imágenes de usuarios
├── pom.xml                          # Configuración Maven
└── README.md
```

---

## ⚙️ Backend

### 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Spring Boot** | 4.0.1 | Framework principal |
| **Java** | 21 | Lenguaje de programación |
| **Spring Data JPA** | - | Persistencia de datos |
| **Spring MVC** | - | Arquitectura web |
| **Spring Cloud OpenFeign** | 2025.1.0 | Cliente HTTP declarativo |
| **Lombok** | - | Reducción de boilerplate |
| **MapStruct** | 1.5.5 | Mapeo Entity-DTO |
| **H2 Database** | - | Base de datos en memoria |
| **Bean Validation** | - | Validación de datos |
| **BCrypt** | - | Encriptación de contraseñas |

### 📦 Entidades del Sistema

El sistema cuenta con **15 entidades JPA** principales:

| Entidad | Descripción |
|---------|-------------|
| `Usuario` | Usuarios del sistema (admin, meseros, etc.) |
| `Perfil` | Perfiles de usuario (roles) |
| `Modulo` | Módulos del sistema |
| `PerfilModulo` | Relación perfil-módulo |
| `Cliente` | Clientes del restaurante |
| `Categoria` | Categorías de productos |
| `Producto` | Productos del menú |
| `Mesa` | Mesas del restaurante |
| `Reserva` | Reservas de mesas |
| `Pedido` | Pedidos realizados |
| `DetallePedido` | Detalles de cada pedido |
| `Pago` | Pagos de pedidos |
| `Resena` | Reseñas de productos |
| `MovimientoInventario` | Movimientos de inventario |
| `EstadoBD` | Enum para estados (ACTIVO/INACTIVO) |

### 🔌 Controladores REST

El backend expone **18 controladores** con endpoints RESTful:

```java
// Controladores principales
@RestController @RequestMapping("/producto")     → ProductoController
@RestController @RequestMapping("/categoria")    → CategoriaController
@RestController @RequestMapping("/usuario")      → UsuarioController
@RestController @RequestMapping("/perfil")       → PerfilController
@RestController @RequestMapping("/modulo")       → ModuloController
@RestController @RequestMapping("/pedido")       → PedidoController
@RestController @RequestMapping("/mesa")         → MesaController
@RestController @RequestMapping("/reserva")      → ReservaController
@RestController @RequestMapping("/pago")         → PagoController
@RestController @RequestMapping("/resena")       → ResenaController
@RestController @RequestMapping("/cliente")      → ClienteController

// Controladores de vistas
@Controller @RequestMapping("/admin")            → AdminController
@Controller @RequestMapping("/api/auth")         → AuthController
@Controller                                      → HomeController
```

### 📁 Estructura de DTOs

**Request DTOs** (entrada de datos):
```
├── CategoriaRequestDTO
├── ProductoRequestDTO
├── UsuarioRequestDTO
├── PedidoRequestDTO
├── ClienteRequestDTO
├── MesaRequestDTO
├── ReservaRequestDTO
├── PagoRequestDTO
├── ResenaRequestDTO
└── ... (14 DTOs en total)
```

**Response DTOs** (salida de datos):
```
├── CategoriaResponseDTO
├── ProductoResponseDTO
├── UsuarioResponseDTO
├── PedidoResponseDTO
├── ClienteResponseDTO
├── MesaResponseDTO
├── ReservaResponseDTO
├── PagoResponseDTO
├── ResenaResponseDTO
└── ... (14 DTOs en total)
```

### 🔒 Configuración de Seguridad

```java
@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 📤 Gestión de Archivos

El sistema incluye `FileStorageService` para manejo de imágenes:

- Subida de imágenes de productos (`/uploads/products/`)
- Subida de imágenes de usuarios (`/uploads/users/`)
- Validación de tipo y tamaño de archivo
- Máximo 5MB por archivo

---

## 🎨 Frontend

### 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Thymeleaf** | 3.x | Motor de plantillas |
| **Bootstrap** | 5.3.2 | Framework CSS |
| **Bootstrap Icons** | 1.11.1 | Iconografía |
| **JavaScript** | ES6+ | Lógica del cliente |
| **CSS3** | - | Estilos personalizados |

### 📄 Estructura de Vistas

```
templates/
├── admin/                          # Panel Administrativo
│   ├── dashboard.html              # Dashboard con estadísticas
│   ├── productos.html              # Gestión de productos
│   ├── categorias.html             # Gestión de categorías
│   └── usuarios.html               # Gestión de usuarios
│
├── auth/                           # Autenticación
│   ├── login.html                  # Login y registro
│   └── admin.html                  # Login administrativo
│
├── cliente/                        # Área de Clientes
│   ├── index.html                  # Página principal
│   ├── catalogo.html               # Catálogo de productos
│   └── resenas.html                # Reseñas de productos
│
└── fragments/                      # Componentes Reutilizables
    ├── head.html                   # Metadatos y estilos
    ├── layout-admin.html           # Layout administrativo
    └── layout-cliente.html         # Layout cliente
```

### 🎨 Archivos CSS

| Archivo | Descripción |
|---------|-------------|
| `variables.css` | Variables CSS (colores, fuentes, etc.) |
| `styles.css` | Estilos globales |
| `admin.css` | Estilos del panel administrativo |
| `cliente.css` | Estilos del área de clientes |

### 📜 Archivos JavaScript

| Archivo | Descripción |
|---------|-------------|
| `admin.js` | Lógica del panel administrativo |
| `catalogo.js` | Filtros y búsqueda del catálogo |
| `categorias.js` | CRUD de categorías |
| `productos.js` | CRUD de productos |
| `usuarios.js` | CRUD de usuarios |
| `cliente.js` | Funcionalidad del cliente |

### 🎯 Características del Frontend

#### Panel Administrativo (Admin)
- **Dashboard** con métricas en tiempo real
  - Ventas del día
  - Total de pedidos
  - Productos activos
  - Calificación promedio
- **Gestión de Productos** con CRUD completo
- **Gestión de Categorías**
- **Gestión de Usuarios**
- **Sidebar colapsable** y responsivo

#### Área de Clientes
- **Página de Inicio** con hero section y platos destacados
- **Catálogo de Productos** con:
  - Filtros por categoría
  - Búsqueda en tiempo real
  - Ordenamiento por precio/popularidad
  - Diseño tipo cards
- **Sistema de Favoritos**
- **Autenticación** (login/registro)

### 🔄 Comunicación Frontend-Backend

```javascript
// Ejemplo: Cargar productos del catálogo
async function cargarProductos() {
    const response = await fetch('/producto');
    const productos = await response.json();
    todosLosProductos = productos.filter(p => p.estadoBD === 'ACTIVO');
    renderizarProductos(todosLosProductos);
}

// Ejemplo: Crear producto con imagen
const formData = new FormData();
formData.append('nombre', nombre);
formData.append('descripcion', descripcion);
formData.append('precioVenta', precio);
formData.append('imagen', archivoImagen);

fetch('/producto', {
    method: 'POST',
    body: formData
});
```

---

## 🚀 Instalación y Configuración

### Prerrequisitos

- **Java 21** o superior
- **Maven 3.9+**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/rest_gastrotech.git
cd qtallisura
```

2. **Compilar el proyecto**
```bash
./mvnw clean install
```

3. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

4. **Acceder a la aplicación**
   - Frontend Cliente: http://localhost:8080/
   - Panel Admin: http://localhost:8080/admin/dashboard
   - Consola H2: http://localhost:8080/h2-console

### ⚙️ Configuración (application.properties)

```properties
# Aplicación
spring.application.name=rest_gastrotech

# Base de datos H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Subida de archivos
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

---

## 📡 Endpoints API REST

### Productos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/producto` | Listar todos los productos |
| `GET` | `/producto/{id}` | Obtener producto por ID |
| `POST` | `/producto` | Crear nuevo producto |
| `PUT` | `/producto/{id}` | Actualizar producto |
| `DELETE` | `/producto/{id}` | Eliminar producto |

### Categorías
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/categoria` | Listar todas las categorías |
| `GET` | `/categoria/{id}` | Obtener categoría por ID |
| `POST` | `/categoria` | Crear nueva categoría |
| `PUT` | `/categoria/{id}` | Actualizar categoría |
| `DELETE` | `/categoria/{id}` | Eliminar categoría |

### Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/usuario` | Listar todos los usuarios |
| `GET` | `/usuario/{id}` | Obtener usuario por ID |
| `POST` | `/usuario` | Crear nuevo usuario |
| `PUT` | `/usuario/{id}` | Actualizar usuario |
| `DELETE` | `/usuario/{id}` | Eliminar usuario |

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Iniciar sesión |
| `POST` | `/api/auth/logout` | Cerrar sesión |

---

## 🗄 Base de Datos

### Diagrama Entidad-Relación (Simplificado)

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   Usuario   │───────│   Perfil    │───────│   Modulo    │
└─────────────┘       └─────────────┘       └─────────────┘
       │
       ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   Pedido    │───────│   Cliente   │       │    Mesa     │
└─────────────┘       └─────────────┘       └─────────────┘
       │                                           │
       ▼                                           ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│DetallePedido│───────│  Producto   │───────│  Categoria  │
└─────────────┘       └─────────────┘       └─────────────┘
       │                    │
       ▼                    ▼
┌─────────────┐       ┌─────────────┐
│    Pago     │       │   Resena    │
└─────────────┘       └─────────────┘
```

### Acceso a Consola H2

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: *(vacío)*

---

## 📸 Capturas de Pantalla

### Página Principal (Cliente)
- Hero section con imagen de fondo
- Platos destacados en carrusel
- Estadísticas del restaurante

### Catálogo de Productos
- Filtros por categoría
- Búsqueda en tiempo real
- Cards de productos con imagen, precio y rating

### Dashboard Administrativo
- Métricas de ventas
- Total de pedidos
- Productos activos
- Calificación promedio

---

## 👥 Autores

- **Renzo** - Desarrollo Full Stack

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

<div align="center">

**⭐ Si te gustó este proyecto, no olvides darle una estrella ⭐**

Hecho con ❤️ usando Spring Boot y Thymeleaf

</div>
