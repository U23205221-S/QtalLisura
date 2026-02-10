# Cambios Realizados: Sidebar del Panel de Administración y Autenticación Unificada

**Fecha:** 10 de Febrero, 2026  
**Objetivo:** 
1. Simplificar el sidebar del panel de administración para mostrar todas las secciones de forma estática
2. Unificar el sistema de autenticación para clientes y usuarios del sistema

---

## 🔧 Cambios en el Backend

### 1. **AdminController.java**
Se agregó el atributo `usuarioLogueado` y `currentPath` al modelo en todos los métodos del controlador:

#### Métodos Actualizados:
- ✅ `dashboard()` - Agregados `usuarioLogueado` y `currentPath`
- ✅ `productos()` - Agregados `usuarioLogueado` y `currentPath`
- ✅ `usuarios()` - Agregados `usuarioLogueado` y `currentPath`
- ✅ `categorias()` - Agregados `usuarioLogueado` y `currentPath`
- ✅ `mesas()` - Agregados `usuarioLogueado` y `currentPath`
- ✅ `reservas()` - Agregados `usuarioLogueado` y `currentPath`

**Código agregado en cada método:**
```java
model.addAttribute("usuarioLogueado", usuario);
model.addAttribute("currentPath", "/admin/{seccion}");
```

**Beneficio:** Esto permite que el layout del sidebar pueda:
1. Mostrar el nombre y foto del usuario logueado en el footer
2. Marcar como activa la sección actual en el menú

---

### 2. **AuthController.java** - Sistema de Autenticación Unificado

#### Endpoints Implementados:

##### ✅ **POST /api/auth/login** (Unificado)
- Acepta tanto usuarios del sistema (por `username`) como clientes (por `DNI`)
- Busca primero en la tabla `Usuario`
- Si no encuentra, busca en la tabla `Cliente` por DNI
- Responde con `redirectUrl` según el tipo de usuario:
  - Administrador → `/admin/dashboard`
  - Cliente → `/catalogo`

**Request Body:**
```json
{
  "username": "admin" o "12345678",
  "password": "contraseña"
}
```

**Response (Usuario):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "idUsuario": 1,
  "nombreCompleto": "Juan Pérez",
  "perfil": "Administrador",
  "redirectUrl": "/admin/dashboard"
}
```

**Response (Cliente):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "idCliente": 1,
  "nombreCompleto": "María García",
  "perfil": "Cliente",
  "redirectUrl": "/catalogo"
}
```

##### ✅ **POST /api/auth/registro**
- Registra nuevos clientes (público)
- Encripta la contraseña automáticamente
- Valida que el DNI no esté duplicado

**Request Body:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "DNI": "12345678",
  "contrasena": "miPassword123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "¡Registro exitoso! Ya puedes iniciar sesión",
  "cliente": {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "DNI": "12345678",
    "estadoBD": "ACTIVO"
  }
}
```

##### ✅ **POST /api/auth/logout**
- Cierra la sesión del usuario o cliente
- Invalida la sesión HTTP

##### ✅ **GET /api/auth/check-session**
- Verifica si hay una sesión activa
- Devuelve información del usuario logueado

##### ✅ **GET /api/auth/current-user**
- Obtiene los datos del usuario actual
- Requiere sesión activa

---

## 🎨 Cambios en el Frontend

### 3. **layout-admin.html** (Fragment Layout)

#### Antes:
El sidebar usaba una lógica compleja con módulos dinámicos basados en permisos:
- Si `modulosPermitidos` tenía datos → mostraba módulos dinámicos
- Si `modulosPermitidos` estaba vacío → mostraba menú estático
- Tenía secciones condicionales que no se renderizaban correctamente

#### Después:
Sidebar simplificado con menú estático siempre visible:

```html
<nav class="sidebar-nav">
    <p class="nav-section-title">Principal</p>
    <ul>
        <li>
            <a th:href="@{/admin/dashboard}">
                <i class="bi bi-grid-1x2-fill"></i>
                <span>Dashboard</span>
            </a>
        </li>
    </ul>

    <p class="nav-section-title">Gestión</p>
    <ul>
        <li><a th:href="@{/admin/productos}">Productos</a></li>
        <li><a th:href="@{/admin/categorias}">Categorías</a></li>
        <li><a th:href="@{/admin/mesas}">Mesas</a></li>
        <li><a th:href="@{/admin/reservas}">Reservas</a></li>
        <li><a th:href="@{/admin/usuarios}">Usuarios</a></li>
    </ul>
</nav>
```

**Secciones del Menú:**
1. **Principal**
   - Dashboard

2. **Gestión**
   - Productos
   - Categorías
   - Mesas
   - Reservas
   - Usuarios

**Íconos Bootstrap utilizados:**
- Dashboard: `bi bi-grid-1x2-fill`
- Productos: `bi bi-box-seam-fill`
- Categorías: `bi bi-tags-fill`
- Mesas: `bi bi-diagram-3-fill`
- Reservas: `bi bi-calendar-check-fill`
- Usuarios: `bi bi-people-fill`

---

### 4. **cliente.js** - Actualización de Endpoints

#### Login de Clientes:
**Antes:**
```javascript
fetch('/cliente/login', {
    method: 'POST',
    body: JSON.stringify({ dni, contrasena: password })
})
```

**Después:**
```javascript
fetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: dni, password: password })
})
```

#### Registro de Clientes:
**Antes:**
```javascript
fetch('/cliente/registro', {
    method: 'POST',
    body: JSON.stringify(data)
})
```

**Después:**
```javascript
fetch('/api/auth/registro', {
    method: 'POST',
    body: JSON.stringify(data)
})
```

---

## 🎯 Funcionalidades

### ✅ Resaltado de Sección Activa
Cada enlace del menú usa `th:classappend` para agregar la clase `active` cuando coincide con la ruta actual:

```html
th:classappend="${currentPath == '/admin/productos'} ? 'active' : ''"
```

### ✅ Footer con Información del Usuario
El footer del sidebar muestra:
- Avatar del usuario (con imagen o iniciales)
- Nombre completo del usuario
- Rol del usuario (ej: "Administrador")

### ✅ Autenticación Unificada
- **Un solo endpoint** `/api/auth/login` para todos los usuarios
- Diferencia automáticamente entre usuarios del sistema y clientes
- Redirige automáticamente según el perfil
- Maneja la sesión HTTP de forma segura

---

## 🚀 Resultado

### Antes:
- ❌ Sidebar vacío (solo mostraba Dashboard y Productos)
- ❌ No se veían las secciones de Mesas, Reservas, Usuarios
- ❌ Lógica compleja con permisos que no funcionaba
- ❌ Endpoints de autenticación inconsistentes

### Después:
- ✅ Sidebar completo con todas las secciones visibles
- ✅ Menú estático simple y funcional
- ✅ Resaltado de sección activa
- ✅ Footer con información del usuario
- ✅ No requiere sistema de permisos complejo
- ✅ Autenticación unificada para clientes y usuarios
- ✅ Endpoints consistentes bajo `/api/auth`

---

## 🔗 Rutas de la Aplicación

### Panel de Administración
| Ruta | Vista | Descripción |
|------|-------|-------------|
| `/admin/dashboard` | `admin/dashboard.html` | Panel principal con estadísticas |
| `/admin/productos` | `admin/productos.html` | Gestión de productos del menú |
| `/admin/categorias` | `admin/categorias.html` | Gestión de categorías de productos |
| `/admin/mesas` | `admin/mesas.html` | Gestión de mesas del restaurante |
| `/admin/reservas` | `admin/reservas.html` | Gestión de reservas de clientes |
| `/admin/usuarios` | `admin/usuarios.html` | Gestión de usuarios y personal |

### Autenticación
| Ruta | Método | Descripción |
|------|--------|-------------|
| `/api/auth/login` | POST | Login unificado (usuarios y clientes) |
| `/api/auth/registro` | POST | Registro de nuevos clientes |
| `/api/auth/logout` | POST | Cierre de sesión |
| `/api/auth/check-session` | GET | Verificar sesión activa |
| `/api/auth/current-user` | GET | Obtener usuario actual |

---

## 🔐 Flujo de Autenticación

### Para Clientes:
1. Cliente ingresa su DNI y contraseña en el formulario
2. Frontend envía a `/api/auth/login` con `username: DNI`
3. Backend busca en tabla `Cliente` por DNI
4. Si existe y la contraseña coincide → sesión activa
5. Redirige a `/catalogo`

### Para Administradores:
1. Admin ingresa su username y contraseña
2. Frontend envía a `/api/auth/login`
3. Backend busca en tabla `Usuario` por username
4. Si existe y la contraseña coincide → sesión activa
5. Redirige a `/admin/dashboard`

---

## ✅ Verificación

Para verificar que los cambios funcionan correctamente:

1. **Inicia la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Accede al panel de administración:**
   ```
   http://localhost:8080/admin/dashboard
   ```

3. **Verifica que el sidebar muestre:**
   - Dashboard (Principal)
   - Productos, Categorías, Mesas, Reservas, Usuarios (Gestión)
   - Información del usuario en el footer

4. **Navega entre secciones:**
   - Verifica que la sección activa se resalte en naranja
   - Verifica que todas las rutas funcionen correctamente

5. **Prueba la autenticación:**
   - Login de admin con username
   - Login de cliente con DNI
   - Registro de nuevo cliente

---

## 📝 Notas Técnicas

- **Framework Frontend:** Bootstrap 5 + Bootstrap Icons
- **Motor de Plantillas:** Thymeleaf
- **Estilos:** CSS personalizado en `admin.css`
- **JavaScript:** `admin.js` y `cliente.js`
- **Seguridad:** BCrypt para encriptación de contraseñas
- **Sesiones:** HTTP Session manejada por Spring

---

**Estado:** ✅ Completado  
**Requiere Compilación:** Sí (la aplicación debe reiniciarse para ver los cambios)


