# 🍽️ Q'Tal Lisura - Módulo de Meseros y Pedidos

## Cambios Realizados

### 📋 Resumen
Se implementó un nuevo perfil **Mesero** con su panel de gestión independiente, un sistema completo de **pedidos** para meseros, y se agregó el módulo de **pedidos al panel de administración**.

---

### 1. 👤 Nuevo Perfil: Mesero

**Archivo:** `src/main/java/com/spring/qtallisura/config/DataLoader.java`

- Se creó el perfil **"Mesero"** con descripción *"Perfil para meseros del restaurante"*.
- Se creó el módulo **"Pedidos"** con descripción *"Gestión de pedidos del restaurante"*.
- Se asignaron los módulos **Dashboard** y **Pedidos** al perfil Mesero (vía `PerfilModulo`).
- Se asignó el módulo **Pedidos** también al perfil Administrador.
- Se creó el usuario semilla:
  - **Username:** `mesero`
  - **Contraseña:** `123456`
  - **DNI:** `87654321`
  - **Perfil:** Mesero

---

### 2. 🔐 Login del Mesero

**Archivos creados:**
- `src/main/resources/templates/auth/mesero.html` — Página de login dedicada para meseros.

**Archivos modificados:**
- `src/main/java/com/spring/qtallisura/controller/HomeController.java` — Nueva ruta `GET /mesero-login` que renderiza el login del mesero.
- `src/main/java/com/spring/qtallisura/controller/AuthController.java`:
  - `determineRedirectUrl()` ahora redirige al perfil **Mesero** hacia `/mesero/dashboard`.
  - `logout()` redirige a `/mesero-login` si el usuario es mesero.
- `src/main/java/com/spring/qtallisura/config/SecurityConfig.java` — Ruta `/mesero-login` agregada como pública.

**Acceso:** `http://localhost:8080/mesero-login`

---

### 3. 🧑‍🍳 Panel del Mesero

**Archivos creados:**

| Archivo | Descripción |
|---------|-------------|
| `controller/MeseroController.java` | Controlador `@RequestMapping("/mesero")` con validación de sesión y perfil |
| `templates/fragments/layout-mesero.html` | Layout con sidebar: Dashboard + Pedidos |
| `templates/mesero/dashboard.html` | Dashboard con estadísticas de pedidos del mesero |
| `templates/mesero/pedidos.html` | Gestión de pedidos: tabla, filtros, modal crear pedido, modal detalle |
| `static/js/mesero.js` | Lógica JS: CRUD pedidos, agregar productos, cambiar estado, filtros |

**Rutas del panel:**
- `GET /mesero/dashboard` — Dashboard con stats (pendientes, en preparación, servidos, pagados)
- `GET /mesero/pedidos` — Tabla de pedidos con acciones

**Características del panel:**
- El mesero **solo ve sus propios pedidos** (filtrado por `idUsuario`).
- Puede **crear** pedidos seleccionando una mesa disponible y agregando productos con cantidades.
- Puede **cambiar el estado** del pedido: `PENDIENTE → EN_PREPARACIÓN → SERVIDO → PAGADO`.
- Puede **cancelar** pedidos en estado pendiente.
- Puede **ver el detalle** de cada pedido con sus productos.
- Filtros por código, estado y fecha.

---

### 4. 📦 Módulo de Pedidos en Admin

**Archivos creados:**

| Archivo | Descripción |
|---------|-------------|
| `templates/admin/pedidos.html` | Vista de todos los pedidos del restaurante |
| `static/js/pedidos-admin.js` | Lógica JS: listar todos, filtrar, ver detalle |

**Archivos modificados:**
- `controller/AdminController.java` — Nuevo endpoint `GET /admin/pedidos`.
- `templates/fragments/layout-admin.html` — Enlace **"Pedidos"** con icono `bi-receipt` agregado al sidebar en la sección "Gestión".

**Características:**
- El admin ve **todos los pedidos** (de todos los meseros).
- Filtros por búsqueda general, estado, fecha y nombre del mesero.
- Modal de detalle con productos del pedido.

---

### 5. 🗃️ Cambios en el Modelo y Backend

#### `Pedido.java`
- `idCliente` ahora es **nullable** (`@JoinColumn(nullable = true)`) para soportar pedidos presenciales sin cliente registrado.

#### `PedidoRequestDTO.java`
- Se eliminó `@NotNull` del campo `idCliente`.

#### `PedidoMapper.java`
- Se agregó null-check en el mapping de `clienteNombre`: si `idCliente` es null, muestra **"Consumidor Final"**.

#### `PedidoService.java`
- **`create()`**: 
  - Cliente es opcional (nullable).
  - Valida que la mesa esté en estado **DISPONIBLE** antes de crear el pedido.
  - Tras crear el pedido, cambia la mesa a **OCUPADA**.
- **`updateById()`**: 
  - Si el estado cambia a **PAGADO** o **CANCELADO**, la mesa vuelve a **DISPONIBLE**.
- **Nuevo método `findByUsuario(Integer idUsuario)`**: Retorna pedidos filtrados por el mesero.

#### `PedidoRepository.java`
- Nuevo: `List<Pedido> findByIdUsuario_IdUsuario(Integer idUsuario)`

#### `PedidoController.java`
- Nuevo endpoint: `GET /pedido/usuario/{idUsuario}` — Pedidos de un mesero específico.

#### `MesaRepository.java`
- Nuevo: `List<Mesa> findByEstadoMesa(Mesa.EstadoMesa estadoMesa)`

#### `MesaService.java`
- Nuevo método: `findByEstado(Mesa.EstadoMesa estado)` — Mesas filtradas por estado.

#### `MesaController.java`
- Nuevo endpoint: `GET /mesa/disponibles` — Solo mesas con estado DISPONIBLE.

#### `DetallePedidoRepository.java`
- Nuevo: `List<DetallePedido> findByIdPedido_IdPedido(Integer idPedido)`

---

### 6. 🔧 Fix Adicional

#### `application.properties`
- Se corrigieron caracteres con encoding roto (ISO-8859-1 → UTF-8) que impedían la compilación con Maven.

---

### 📁 Estructura de Archivos Nuevos

```
src/main/java/com/spring/qtallisura/
└── controller/
    └── MeseroController.java          ← NUEVO

src/main/resources/
├── static/js/
│   ├── mesero.js                      ← NUEVO
│   └── pedidos-admin.js               ← NUEVO
└── templates/
    ├── auth/
    │   └── mesero.html                ← NUEVO
    ├── admin/
    │   └── pedidos.html               ← NUEVO
    ├── fragments/
    │   └── layout-mesero.html         ← NUEVO
    └── mesero/
        ├── dashboard.html             ← NUEVO
        └── pedidos.html               ← NUEVO
```

---

### 🔄 Flujo de Trabajo del Mesero

```
1. Login en /mesero-login (mesero / 123456)
2. Dashboard → Ver resumen de pedidos propios
3. Pedidos → Nuevo Pedido:
   a. Seleccionar mesa disponible
   b. Agregar productos con cantidades
   c. Guardar → Mesa pasa a OCUPADA
4. Gestionar estados:
   PENDIENTE → EN_PREPARACIÓN → SERVIDO → PAGADO
                                        ↘ (mesa vuelve a DISPONIBLE)
   PENDIENTE → CANCELADO → (mesa vuelve a DISPONIBLE)
```

---

### 🔄 Flujo de Trabajo del Admin (Pedidos)

```
1. Login en /admin (admin / 123456)
2. Sidebar → Pedidos
3. Ver TODOS los pedidos de todos los meseros
4. Filtrar por estado, fecha, mesero
5. Ver detalle de cada pedido con sus productos
```

