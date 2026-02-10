# 🍽️ Q'Tal Lisura - Flujo de la Aplicación

> Sistema de gestión para restaurante peruano con catálogo informativo y reservas online

---

## 🎯 Propósito del Sistema

**Q'Tal Lisura** es una aplicación web para restaurantes que permite:
1. 📖 **Mostrar el menú** a los clientes (sin venta online)
2. 📅 **Gestionar reservas** de mesas
3. 👨‍💼 **Administrar** productos, categorías, mesas y usuarios

---

## 🔄 Flujo General de la Aplicación

```
┌─────────────────────────────────────────────────────────────┐
│                     USUARIO VISITA EL SITIO                 │
│                      www.qtallisura.com                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
           ┌───────────────────────┐
           │   ¿TIPO DE USUARIO?   │
           └───────────┬───────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
┌───────────────┐            ┌─────────────────┐
│   CLIENTE     │            │  ADMINISTRADOR  │
│   (Público)   │            │   (Staff)       │
└───────┬───────┘            └────────┬────────┘
        │                             │
        │                             │
   ┌────┴────┐                   ┌────┴────┐
   │         │                   │         │
   ▼         ▼                   ▼         ▼
[VER]    [RESERVAR]         [GESTIONAR] [REPORTES]
```

---

## 👤 Flujo del Cliente

### 1️⃣ Ver Catálogo
```
Cliente → /catalogo → GET /producto → Muestra productos activos
                                    → Filtro por categoría
                                    → Búsqueda por nombre
                                    → NO hay carrito de compras
```

**Características:**
- ✅ Ver fotos de platos
- ✅ Ver precios
- ✅ Leer descripciones
- ✅ Filtrar por categorías (Entradas, Platos Fondo, Postres, Bebidas)
- ❌ NO se puede agregar al carrito
- ❌ NO se puede comprar online

### 2️⃣ Hacer Reserva
```
Cliente → /reservas → Llena formulario → POST /reserva → Confirmación
          │
          ├─ GET /mesa (mesas disponibles)
          ├─ Selecciona fecha/hora
          ├─ Indica cantidad de personas
          ├─ Selecciona mesa según capacidad
          └─ Envía solicitud (estado: PENDIENTE)
```

**Campos del Formulario:**
- 👤 Nombre completo
- 📞 Teléfono
- 📅 Fecha y hora (mínimo 1 hora en el futuro)
- 👥 Cantidad de personas (1-20)
- 🪑 Mesa (se filtra automáticamente por capacidad)
- 💬 Comentarios opcionales

**Estados de Reserva:**
- 🟡 **PENDIENTE** → Esperando confirmación del restaurante
- 🟢 **CONFIRMADA** → Aprobada por el admin
- 🔴 **CANCELADA** → Rechazada o cancelada
- ✅ **COMPLETADA** → Cliente ya asistió

---

## 👨‍💼 Flujo del Administrador

### 🔐 Login
```
Admin → /auth/admin → Ingresa credenciales → POST /api/auth/login
                                           → Verifica usuario/contraseña
                                           → Crea sesión HTTP
                                           → Redirige a /admin/dashboard
```

### 📊 Dashboard
```
/admin/dashboard
├─ 📈 Estadísticas generales
├─ 📋 Reservas del día
├─ 📦 Productos con stock bajo
└─ 👥 Usuarios activos
```

### 🍽️ Gestión de Productos
```
/admin/productos
│
├─ [Crear Producto]
│   └─ POST /producto (con imagen)
│
├─ [Editar Producto]
│   ├─ GET /producto/{id}
│   └─ PUT /producto/{id}
│
├─ [Eliminar Producto]
│   └─ DELETE /producto/{id}
│
└─ [Filtros]
    ├─ Por categoría
    ├─ Por stock
    └─ Por estado (Activo/Inactivo)
```

**Campos de Producto:**
- Nombre
- Descripción
- Categoría
- Precio de venta
- Costo unitario
- Stock mínimo / actual
- Imagen (opcional)
- Estado (ACTIVO/INACTIVO)

### 🏷️ Gestión de Categorías
```
/admin/categorias
│
└─ CRUD completo
    ├─ POST /categoria
    ├─ GET /categoria
    ├─ PUT /categoria/{id}
    └─ DELETE /categoria/{id}
```

### 🪑 Gestión de Mesas
```
/admin/mesas
│
└─ CRUD completo
    ├─ POST /mesa
    ├─ GET /mesa
    ├─ PUT /mesa/{id}
    └─ DELETE /mesa/{id}
```

**Campos de Mesa:**
- Número de mesa
- Capacidad máxima
- Ubicación (Primer/Segundo piso)
- Estado de mesa (DISPONIBLE/OCUPADA/RESERVADA/EN_MANTENIMIENTO)
- Estado BD (ACTIVO/INACTIVO)

### 📋 Gestión de Reservas
```
/admin/reservas
│
├─ [Ver todas las reservas]
│   └─ GET /reserva
│
├─ [Filtrar reservas]
│   ├─ Por estado
│   ├─ Por mesa
│   ├─ Por fecha
│   └─ Por búsqueda (nombre/teléfono)
│
├─ [Confirmar reserva]
│   └─ PUT /reserva/{id} → estadoSolicitud: "CONFIRMADA"
│
├─ [Cancelar reserva]
│   └─ PUT /reserva/{id} → estadoSolicitud: "CANCELADA"
│
└─ [Ver estadísticas]
    ├─ Pendientes
    ├─ Confirmadas
    ├─ Canceladas
    └─ Para hoy
```

### 👥 Gestión de Usuarios
```
/admin/usuarios
│
└─ CRUD completo
    ├─ POST /usuario (con avatar)
    ├─ GET /usuario
    ├─ PUT /usuario/{id}
    └─ DELETE /usuario/{id}
```

**Tipos de Usuario (Perfiles):**
- 👔 ADMINISTRADOR → Acceso total
- 👨‍🍳 COCINERO → Gestión de productos/pedidos
- 🧑‍💼 MESERO → Gestión de mesas/reservas
- 👤 CLIENTE → Solo frontend público

---

## 🔗 Mapa de Rutas

### Frontend (Vistas Públicas)
| Ruta | Vista | Descripción |
|------|-------|-------------|
| `/` | Inicio | Landing page del restaurante |
| `/catalogo` | Catálogo | Menú completo (solo lectura) |
| `/reservas` | Reservas | Formulario para reservar mesa |
| `/nosotros` | Nosotros | Historia del restaurante |
| `/resenas` | Reseñas | Opiniones de clientes |

### Frontend (Admin)
| Ruta | Vista | Descripción |
|------|-------|-------------|
| `/auth/admin` | Login | Autenticación de staff |
| `/admin/dashboard` | Dashboard | Panel principal |
| `/admin/productos` | Productos | CRUD de productos |
| `/admin/categorias` | Categorías | CRUD de categorías |
| `/admin/mesas` | Mesas | CRUD de mesas |
| `/admin/reservas` | Reservas | Gestión de reservas |
| `/admin/usuarios` | Usuarios | CRUD de usuarios |

### Backend (API REST)
| Endpoint | Métodos | Descripción |
|----------|---------|-------------|
| `/producto` | GET, POST, PUT, DELETE | API de productos |
| `/categoria` | GET, POST, PUT, DELETE | API de categorías |
| `/mesa` | GET, POST, PUT, DELETE | API de mesas |
| `/reserva` | GET, POST, PUT, DELETE | API de reservas |
| `/usuario` | GET, POST, PUT, DELETE | API de usuarios |
| `/perfil` | GET, POST, PUT, DELETE | API de perfiles |
| `/cliente` | GET, POST, PUT, DELETE | API de clientes |
| `/api/auth/login` | POST | Autenticación |
| `/api/auth/logout` | POST | Cerrar sesión |

---

## 🎨 Stack Tecnológico

### Backend
- ☕ **Java 17+**
- 🍃 **Spring Boot** (Web, Data JPA, Security)
- 🗄️ **JPA/Hibernate** (ORM)
- 🔒 **BCrypt** (Encriptación)
- 📦 **Lombok** (Boilerplate reduction)

### Frontend
- 🎨 **Thymeleaf** (Template engine)
- 🟨 **JavaScript Vanilla** (No frameworks)
- 🎯 **Bootstrap 5** (UI framework)
- 🎭 **Bootstrap Icons**
- 📱 **Responsive Design**

### Herramientas
- 🔨 **Maven** (Build tool)
- 🐳 **OpenFeign** (HTTP client)
- 📁 **FileStorage** (Gestión de uploads)

---

## 📱 Experiencia de Usuario

### Cliente en Móvil
```
1. Abre www.qtallisura.com
2. Ve el carrusel de platos destacados
3. Toca "Ver Menú" → Catálogo responsive
4. Filtra por "Postres"
5. Ve fotos y precios
6. Decide visitar el restaurante
7. Toca "Reservar Mesa"
8. Llena formulario en 1 minuto
9. Recibe confirmación: "Reserva registrada, te contactaremos pronto"
```

### Admin en Desktop
```
1. Ingresa a /auth/admin
2. Login con usuario/contraseña
3. Ve dashboard con métricas
4. Nota: 3 reservas pendientes
5. Ingresa a "Reservas"
6. Filtra por "Pendientes"
7. Revisa disponibilidad
8. Confirma 2 reservas
9. Cancela 1 (mesa no disponible)
10. Clientes reciben notificación (futuro: email/SMS)
```

---

## 🔐 Seguridad

### Implementado
- ✅ Autenticación con sesiones HTTP
- ✅ Contraseñas encriptadas (BCrypt)
- ✅ Validación de entrada (DTOs)
- ✅ Validación de archivos (tipo y tamaño)
- ✅ Sanitización de inputs en frontend
- ✅ Control de acceso por perfil

### Por Implementar (Futuro)
- 🔲 JWT para API stateless
- 🔲 Rate limiting
- 🔲 CSRF tokens
- 🔲 HTTPS obligatorio
- 🔲 2FA para admins

---

## 📊 Casos de Uso Principales

### 1. Cliente reserva mesa
```
Precondición: Cliente desea cenar el viernes a las 8pm con 4 personas
Flujo:
  1. Ingresa a /reservas
  2. Selecciona fecha: 2026-02-14, hora: 20:00
  3. Indica 4 personas
  4. El sistema muestra mesas con capacidad ≥4
  5. Selecciona "Mesa 5 - 6 personas (Primer Piso)"
  6. Ingresa datos de contacto
  7. Envía formulario
  8. Sistema crea reserva con estado PENDIENTE
Resultado: Reserva registrada, admin recibirá notificación
```

### 2. Admin confirma reserva
```
Precondición: Hay reservas pendientes
Flujo:
  1. Admin ingresa a /admin/reservas
  2. Ve estadísticas: 5 pendientes
  3. Filtra por "Pendientes"
  4. Revisa reserva de Juan Pérez
  5. Verifica que Mesa 5 esté libre
  6. Clica botón "Confirmar"
  7. Sistema actualiza estado a CONFIRMADA
Resultado: Cliente recibirá confirmación (futuro: por email)
```

### 3. Admin gestiona productos
```
Precondición: Nuevo plato en el menú
Flujo:
  1. Admin ingresa a /admin/productos
  2. Clica "Nuevo Producto"
  3. Llena formulario:
     - Nombre: "Tacu Tacu con Lomo"
     - Categoría: Platos de Fondo
     - Precio: S/ 28.00
     - Sube foto del plato
  4. Guarda
  5. Sistema valida imagen y datos
  6. Crea producto con estado ACTIVO
Resultado: Producto visible inmediatamente en catálogo público
```

---

## 📈 Flujo de Datos

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │ HTTP Request
       ▼
┌─────────────────┐
│  Controller     │ (Spring MVC)
│  - Valida input │
│  - Llama Service│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Service      │ (Lógica de negocio)
│  - Procesa datos│
│  - Aplica reglas│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Repository    │ (JPA)
│  - Consulta DB  │
│  - Persiste     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Base de Datos  │
│  - MySQL/H2     │
└─────────────────┘
```

---

## 🎯 Características Destacadas

### ✨ Para el Cliente
- 📱 **Responsive**: Funciona en móvil, tablet y desktop
- 🔍 **Búsqueda inteligente**: Encuentra platos por nombre
- 🏷️ **Filtros rápidos**: Por categorías
- ⚡ **Sin recargas**: JavaScript asíncrono (fetch)
- ✅ **Validación en tiempo real**: Formularios inteligentes

### 🛠️ Para el Admin
- 📊 **Dashboard intuitivo**: Métricas en tiempo real
- 🖼️ **Gestión de imágenes**: Subida drag & drop
- 🔄 **Actualización instantánea**: Sin recargar página
- 🎨 **UI moderna**: Bootstrap 5
- 📋 **Filtros avanzados**: Múltiples criterios

---

## 🚀 Ventajas del Sistema Actual

| Aspecto | Beneficio |
|---------|-----------|
| **Sin carrito** | Mayor simpleza, enfoque en reservas |
| **Catálogo informativo** | Clientes ven menú antes de visitar |
| **Reservas online** | Reduce llamadas telefónicas |
| **Gestión centralizada** | Todo en un solo sistema |
| **Escalable** | Fácil agregar nuevas funcionalidades |

---

## 🔮 Próximas Mejoras Sugeridas

1. **Notificaciones Automáticas**
   - 📧 Email al confirmar/cancelar reserva
   - 📱 SMS recordatorios

2. **Pasarela de Pago** (si se reactiva ventas)
   - 💳 Integración con Culqi/Mercado Pago
   - 💰 Depósito para reservar

3. **QR Codes**
   - 🎫 Código QR en confirmación de reserva
   - 📲 Escanear al llegar al restaurante

4. **Analytics**
   - 📈 Reportes de productos más vistos
   - 📊 Horas pico de reservas
   - 🎯 Preferencias de clientes

5. **Sistema de Reseñas**
   - ⭐ Clientes califican su experiencia
   - 💬 Comentarios públicos
   - 🏆 Platos mejor valorados

---

## 📞 Soporte

Para más información técnica, consulta:
- 📄 `CAMBIOS_REALIZADOS.md` → Documentación detallada de cambios
- 📄 `README.md` → Guía de instalación y ejecución
- 📄 `README_COMPLETO.md` → Documentación completa del proyecto

---

**🍽️ Q'Tal Lisura** - Donde la tecnología se encuentra con la gastronomía peruana  
*Versión 1.0 - Febrero 2026*

