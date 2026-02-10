# 🍽️ Q'Tal Lisura - Sistema de Gestión para Restaurante

> Plataforma web para restaurante peruano con catálogo informativo y gestión de reservas

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple.svg)](https://getbootstrap.com/)
[![Status](https://img.shields.io/badge/Status-Funcionando-success.svg)]()

---

## 📖 Índice de Documentación

Este proyecto cuenta con documentación completa organizada en varios archivos:

### 📋 Documentos Principales

1. **[RESUMEN_EJECUTIVO.md](./RESUMEN_EJECUTIVO.md)** ⚡
   - Resumen rápido de cambios realizados
   - Tabla comparativa Antes/Después
   - Estado actual del proyecto
   - **Recomendado para:** Revisión rápida

2. **[README_FLUJO.md](./README_FLUJO.md)** 🔄
   - Flujo general de la aplicación
   - Casos de uso principales
   - Experiencia de usuario (Cliente y Admin)
   - Mapa de rutas completo
   - **Recomendado para:** Entender cómo funciona el sistema

3. **[CAMBIOS_REALIZADOS.md](./CAMBIOS_REALIZADOS.md)** 📝
   - Documentación técnica detallada
   - Cambios específicos por archivo
   - Arquitectura del proyecto
   - Mapeo de endpoints
   - **Recomendado para:** Desarrolladores

4. **[DIAGRAMAS_FLUJO.md](./DIAGRAMAS_FLUJO.md)** 📊
   - Diagramas técnicos ASCII
   - Flujo de autenticación
   - Flujo de reservas
   - Ciclo de vida de entidades
   - **Recomendado para:** Análisis técnico profundo

5. **[README_COMPLETO.md](./README_COMPLETO.md)** 📚
   - Documentación original del proyecto
   - Información adicional del sistema

---

## 🚀 Inicio Rápido

### Prerrequisitos

- ☕ Java 17 o superior
- 🔨 Maven 3.6+
- 🗄️ MySQL o H2 (base de datos)
- 🌐 Navegador moderno

### Instalación

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/QtalLisura.git
cd QtalLisura

# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run
```

### Acceso

- **Frontend Público:** http://localhost:8080/
- **Panel Admin:** http://localhost:8080/auth/admin
- **Catálogo:** http://localhost:8080/catalogo
- **Reservas:** http://localhost:8080/reservas

### Credenciales por Defecto

```
Usuario Admin:
username: admin
password: admin123
```

---

## 🎯 ¿Qué hace este Sistema?

**Q'Tal Lisura** es una aplicación web completa para restaurantes que permite:

### 👤 Para Clientes:
- ✅ Ver el catálogo de productos (menú del restaurante)
- ✅ Filtrar por categorías (Entradas, Platos Fondo, Postres, Bebidas)
- ✅ Buscar platos específicos
- ✅ **Reservar mesas online** con validación en tiempo real
- ❌ NO permite comprar online (solo informativo)

### 👨‍💼 Para Administradores:
- ✅ CRUD completo de **Productos** (con imágenes)
- ✅ CRUD completo de **Categorías**
- ✅ CRUD completo de **Mesas** (capacidad, ubicación, estado)
- ✅ **Gestión de Reservas** (confirmar, cancelar, ver estadísticas)
- ✅ CRUD completo de **Usuarios** (con perfiles y avatares)
- ✅ Dashboard con métricas en tiempo real

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│         FRONTEND (Thymeleaf + JS)           │
│  ├─ HTML Templates                          │
│  ├─ JavaScript Vanilla                      │
│  └─ Bootstrap 5 + CSS Custom                │
└─────────────────┬───────────────────────────┘
                  │ HTTP/JSON
┌─────────────────▼───────────────────────────┐
│         BACKEND (Spring Boot)               │
│  ├─ Controllers (REST + Views)              │
│  ├─ Services (Business Logic)               │
│  ├─ Repositories (JPA)                      │
│  └─ Security (BCrypt + Sessions)            │
└─────────────────┬───────────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────────┐
│         BASE DE DATOS (MySQL/H2)            │
│  ├─ Productos    ├─ Mesas                   │
│  ├─ Categorías   ├─ Reservas                │
│  └─ Usuarios     └─ Clientes                │
└─────────────────────────────────────────────┘
```

---

## 📂 Estructura del Proyecto

```
QtalLisura/
├── src/
│   ├── main/
│   │   ├── java/com/spring/qtallisura/
│   │   │   ├── controller/          # Controladores REST y vistas
│   │   │   ├── service/             # Lógica de negocio
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   ├── model/               # Entidades
│   │   │   └── dto/                 # Data Transfer Objects
│   │   └── resources/
│   │       ├── templates/           # Vistas Thymeleaf
│   │       │   ├── cliente/         # Frontend público
│   │       │   ├── admin/           # Panel administrativo
│   │       │   └── auth/            # Páginas de login
│   │       ├── static/
│   │       │   ├── js/              # JavaScript
│   │       │   ├── css/             # Estilos
│   │       │   └── images/          # Imágenes estáticas
│   │       └── application.properties
│   └── test/                        # Tests unitarios
├── uploads/                         # Archivos subidos
│   ├── products/                    # Imágenes de productos
│   └── users/                       # Avatares de usuarios
├── pom.xml                          # Dependencias Maven
└── README.md                        # Este archivo
```

---

## 🔗 Endpoints Principales

### Frontend (Vistas)
- `GET /` - Página principal
- `GET /catalogo` - Catálogo de productos
- `GET /reservas` - Formulario de reservas
- `GET /auth/admin` - Login administrador
- `GET /admin/dashboard` - Panel de control

### API REST (Backend)
- `GET|POST|PUT|DELETE /producto` - CRUD productos
- `GET|POST|PUT|DELETE /categoria` - CRUD categorías
- `GET|POST|PUT|DELETE /mesa` - CRUD mesas
- `GET|POST|PUT|DELETE /reserva` - CRUD reservas
- `GET|POST|PUT|DELETE /usuario` - CRUD usuarios
- `POST /api/auth/login` - Autenticación
- `POST /api/auth/logout` - Cerrar sesión

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - ORM
- **Spring Security** - Autenticación
- **Hibernate** - Implementación JPA
- **Lombok** - Reducción de boilerplate
- **BCrypt** - Encriptación de contraseñas
- **OpenFeign** - Cliente HTTP
- **Validation API** - Validación de datos

### Frontend
- **Thymeleaf** - Motor de templates
- **JavaScript Vanilla** - Lógica del cliente
- **Bootstrap 5** - Framework CSS
- **Bootstrap Icons** - Iconografía
- **Fetch API** - Peticiones AJAX

### Base de Datos
- **MySQL** - Producción
- **H2** - Desarrollo/Testing

---

## 📊 Características Destacadas

### ✨ Interfaz de Usuario
- 📱 **Responsive Design** - Funciona en móvil, tablet y desktop
- 🎨 **UI Moderna** - Bootstrap 5 con personalización
- ⚡ **Sin Recargas** - Actualización dinámica con JavaScript
- 🔍 **Filtros en Tiempo Real** - Búsqueda instantánea
- ✅ **Validación Inteligente** - Feedback inmediato en formularios

### 🔒 Seguridad
- 🔐 **Autenticación BCrypt** - Contraseñas encriptadas
- 🛡️ **Sesiones HTTP** - Manejo de estado seguro
- ✅ **Validación Doble** - Cliente y servidor
- 🚫 **Control de Acceso** - Por roles (Admin, Usuario)

### 📈 Gestión
- 📊 **Dashboard Estadístico** - Métricas en tiempo real
- 🖼️ **Gestión de Imágenes** - Subida y validación
- 🔄 **Estados de Reserva** - PENDIENTE → CONFIRMADA → COMPLETADA
- 📋 **Filtros Avanzados** - Múltiples criterios

---

## 🔄 Cambios Recientes (Febrero 2026)

### ✅ Correcciones Implementadas

1. **Alineación de Rutas API**
   - Corregidas rutas de `/api/productos` a `/producto`
   - Eliminado prefijo `/api/` innecesario (excepto auth)
   - Cambiado de plural a singular en endpoints

2. **Eliminación de Carrito**
   - Removida funcionalidad de compras online
   - Catálogo ahora es solo informativo
   - Enfoque en sistema de reservas

3. **Documentación Completa**
   - 4 documentos técnicos creados
   - Diagramas de flujo incluidos
   - Casos de uso documentados

Para más detalles, ver **[CAMBIOS_REALIZADOS.md](./CAMBIOS_REALIZADOS.md)**

---

## 📚 Guía de Uso

### Para Desarrolladores

1. **Revisar arquitectura:** [CAMBIOS_REALIZADOS.md](./CAMBIOS_REALIZADOS.md)
2. **Ver diagramas técnicos:** [DIAGRAMAS_FLUJO.md](./DIAGRAMAS_FLUJO.md)
3. **Entender endpoints:** Ver sección "Mapeo de Endpoints"
4. **Configurar entorno:** Ver `application.properties`

### Para Product Owners

1. **Ver flujo del sistema:** [README_FLUJO.md](./README_FLUJO.md)
2. **Revisar casos de uso:** Sección "Casos de Uso"
3. **Estado del proyecto:** [RESUMEN_EJECUTIVO.md](./RESUMEN_EJECUTIVO.md)

---

## 🚀 Próximas Mejoras

- [ ] Notificaciones por email/SMS al confirmar reservas
- [ ] Códigos QR para reservas
- [ ] Sistema de reseñas interactivo
- [ ] Pasarela de pago (si se reactivan ventas)
- [ ] Analytics y reportes avanzados
- [ ] API pública con Swagger/OpenAPI
- [ ] Autenticación JWT

---

## 🐛 Solución de Problemas

### Error: "Cannot resolve /api/productos"
✅ **Solucionado** - Todas las rutas corregidas a `/producto`

### Carrito de compras no funciona
✅ **Intencionado** - Funcionalidad removida, ahora es solo informativo

### Imágenes no se suben
- Verificar permisos en carpeta `uploads/`
- Revisar tamaño máximo (5MB)
- Formatos permitidos: JPG, JPEG, PNG, WEBP

---

## 👥 Contribución

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es privado y está protegido por derechos de autor.

---

## 📞 Contacto y Soporte

Para soporte técnico o consultas:
- 📧 Email: soporte@qtallisura.com
- 📱 WhatsApp: +51 987 654 321
- 🌐 Web: www.qtallisura.com

---

## 🙏 Agradecimientos

- **Spring Boot Team** - Excelente framework
- **Bootstrap Team** - UI framework
- **Comunidad de desarrolladores** - Soporte y recursos

---

<div align="center">

**🍽️ Q'Tal Lisura**  
*Donde la tecnología se encuentra con la gastronomía peruana*

[![Made with Spring Boot](https://img.shields.io/badge/Made%20with-Spring%20Boot-brightgreen.svg)](https://spring.io/)
[![Made with Love](https://img.shields.io/badge/Made%20with-❤️-red.svg)]()

**Versión 1.0 - Febrero 2026**

[Ver Documentación](./CAMBIOS_REALIZADOS.md) • [Flujos del Sistema](./README_FLUJO.md) • [Diagramas](./DIAGRAMAS_FLUJO.md)

</div>
