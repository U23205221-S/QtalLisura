# Q'Tal Lisura - Sistema de Gestion de Restaurante

Sistema de gestion integral para restaurantes desarrollado con Spring Boot. Permite gestionar pedidos, mesas, reservas, productos, usuarios y clientes.

## Tabla de Contenidos

- [Tecnologias](#tecnologias)
- [Requisitos Previos](#requisitos-previos)
- [Instalacion](#instalacion)
  - [Opcion 1: Docker (Recomendado)](#opcion-1-docker-recomendado)
  - [Opcion 2: XAMPP](#opcion-2-xampp)
- [Ejecutar la Aplicacion](#ejecutar-la-aplicacion)
- [Usuarios por Defecto](#usuarios-por-defecto)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Arquitectura](#arquitectura)
- [Entidades y Relaciones](#entidades-y-relaciones)
- [Endpoints API](#endpoints-api)
- [Comandos Utiles](#comandos-utiles)

---

## Tecnologias

| Tecnologia | Version | Proposito |
|------------|---------|-----------|
| Java | 21 | Lenguaje de programacion |
| Spring Boot | 4.0.1 | Framework principal |
| Spring Data JPA | - | Persistencia de datos |
| Spring Security | - | Seguridad |
| Thymeleaf | - | Motor de plantillas |
| MySQL | 8.0 | Base de datos |
| MapStruct | 1.5.5 | Mapeo DTO/Entity |
| Lombok | - | Reduccion de boilerplate |
| Maven | - | Gestion de dependencias |

---

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+** (o usar el wrapper incluido `./mvnw`)
- **Docker y Docker Compose** (Opcion 1) o **XAMPP** (Opcion 2)

### Verificar Java
```bash
java -version
# Debe mostrar: openjdk version "21.x.x" o similar
```

---

## Instalacion

### Opcion 1: Docker (Recomendado)

Esta opcion utiliza Docker para ejecutar MySQL y phpMyAdmin.

#### Paso 1: Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd QtalLisura
```

#### Paso 2: Iniciar los contenedores Docker
```bash
# Iniciar MySQL y phpMyAdmin en segundo plano
docker-compose up -d

# Verificar que los contenedores esten corriendo
docker-compose ps
```

**Servicios disponibles:**
| Servicio | Puerto | Descripcion |
|----------|--------|-------------|
| MySQL | 3306 | Base de datos |
| phpMyAdmin | 8081 | Administrador de BD (http://localhost:8081) |

#### Paso 3: Verificar conexion a MySQL
```bash
# Esperar unos segundos para que MySQL inicie completamente
docker-compose logs -f mysql

# Una vez que vea "ready for connections", presione Ctrl+C
```

#### Paso 4: Ejecutar la aplicacion
```bash
./mvnw spring-boot:run
```

#### Comandos Docker utiles
```bash
# Detener los contenedores
docker-compose down

# Detener y eliminar volumenes (BORRA TODOS LOS DATOS)
docker-compose down -v

# Ver logs de MySQL
docker-compose logs -f mysql

# Reiniciar MySQL
docker-compose restart mysql

# Acceder a MySQL por consola
docker exec -it qtallisura-mysql mysql -u root -proot dbrestaurant
```

---

### Opcion 2: XAMPP

Esta opcion utiliza XAMPP para MySQL/MariaDB.

#### Paso 1: Instalar XAMPP

1. Descargar XAMPP desde: https://www.apachefriends.org/
2. Instalar con las opciones:
   - **MySQL** (requerido)
   - **phpMyAdmin** (recomendado)

#### Paso 2: Iniciar servicios

1. Abrir **XAMPP Control Panel**
2. Iniciar **MySQL** (clic en "Start")
3. (Opcional) Iniciar **Apache** si desea usar phpMyAdmin

#### Paso 3: Crear la base de datos

**Opcion A: Usando phpMyAdmin**
1. Abrir http://localhost/phpmyadmin
2. Clic en "Nueva" (panel izquierdo)
3. Nombre de la base de datos: `dbrestaurant`
4. Cotejamiento: `utf8mb4_general_ci`
5. Clic en "Crear"

**Opcion B: Usando terminal MySQL**
```bash
# En Windows, abrir CMD y ejecutar:
cd C:\xampp\mysql\bin
mysql -u root

# Una vez dentro de MySQL:
CREATE DATABASE dbrestaurant CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
EXIT;
```

**Opcion C: Usando MySQL Shell de XAMPP**
1. En XAMPP Control Panel, clic en "Shell"
2. Ejecutar:
```bash
mysql -u root -e "CREATE DATABASE dbrestaurant CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
```

#### Paso 4: Configurar credenciales (si es necesario)

Por defecto, XAMPP usa:
- **Usuario:** root
- **Password:** (vacio)

Si tu XAMPP tiene password vacio, modifica `src/main/resources/application.properties`:

```properties
# Cambiar estas lineas:
spring.datasource.username=root
spring.datasource.password=root

# Por estas (password vacio):
spring.datasource.username=root
spring.datasource.password=
```

> **Nota:** Si prefieres mantener el password `root`, configura uno en XAMPP:
> 1. Abrir phpMyAdmin (http://localhost/phpmyadmin)
> 2. Ir a "Cuentas de usuario"
> 3. Editar el usuario `root` y establecer password `root`

#### Paso 5: Ejecutar la aplicacion

```bash
# En la carpeta del proyecto
./mvnw spring-boot:run

# En Windows, usar:
mvnw.cmd spring-boot:run
```

---

## Ejecutar la Aplicacion

Una vez que MySQL este corriendo (Docker o XAMPP):

```bash
# Desarrollo
./mvnw spring-boot:run

# O compilar y ejecutar JAR
./mvnw clean package -DskipTests
java -jar target/QtalLisura-0.0.1-SNAPSHOT.jar
```

**La aplicacion estara disponible en:** http://localhost:8080

### URLs Principales

| URL | Descripcion |
|-----|-------------|
| http://localhost:8080 | Pagina principal |
| http://localhost:8080/catalogo | Catalogo de productos |
| http://localhost:8080/login | Login de clientes |
| http://localhost:8080/admin | Login de administradores |
| http://localhost:8080/mesero-login | Login de meseros |
| http://localhost:8080/admin/dashboard | Panel de administracion |
| http://localhost:8080/mesero/dashboard | Panel de meseros |

---

## Usuarios por Defecto

Al iniciar la aplicacion se crean automaticamente:

| Tipo | Usuario | Password | Acceso |
|------|---------|----------|--------|
| Administrador | `admin` | `123456` | Panel Admin completo |
| Mesero | `mesero` | `123456` | Panel Mesero |
| Cliente | (DNI) | (registrado) | Area de clientes |

---

## Estructura del Proyecto

```
QtalLisura/
├── docker-compose.yml        # Configuracion Docker
├── pom.xml                   # Dependencias Maven
├── mvnw / mvnw.cmd           # Maven Wrapper
├── uploads/                  # Archivos subidos
│   ├── users/                # Imagenes de usuarios
│   └── products/             # Imagenes de productos
└── src/
    ├── main/
    │   ├── java/com/spring/qtallisura/
    │   │   ├── config/           # Configuraciones
    │   │   ├── controller/       # Controladores REST y Web
    │   │   ├── dto/
    │   │   │   ├── request/      # DTOs de entrada
    │   │   │   └── response/     # DTOs de salida
    │   │   ├── exception/        # Manejo de errores
    │   │   ├── mapper/           # MapStruct mappers
    │   │   ├── model/            # Entidades JPA
    │   │   ├── repository/       # Repositorios Spring Data
    │   │   ├── service/          # Logica de negocio
    │   │   └── utility/          # Utilidades
    │   └── resources/
    │       ├── application.properties
    │       ├── templates/        # Plantillas Thymeleaf
    │       │   ├── admin/        # Vistas admin
    │       │   ├── auth/         # Vistas login
    │       │   ├── cliente/      # Vistas publicas
    │       │   ├── fragments/    # Componentes reutilizables
    │       │   └── mesero/       # Vistas mesero
    │       └── static/
    │           ├── css/
    │           ├── js/
    │           └── images/
    └── test/                     # Tests
```

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTACION                            │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   │
│   │Web Controllers│   │REST Controllers│   │ Thymeleaf   │   │
│   └───────┬──────┘   └───────┬──────┘   └──────────────┘   │
└───────────┼──────────────────┼──────────────────────────────┘
            └────────┬─────────┘
                     ▼
            ┌────────────────┐
            │ DTOs (Request/ │
            │   Response)    │
            └───────┬────────┘
                    ▼
┌──────────────────────────────────────────────────────────────┐
│                      NEGOCIO                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                    Services                          │   │
│   └────────────────────────┬────────────────────────────┘   │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              MapStruct Mappers                       │   │
│   └─────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                    PERSISTENCIA                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │          Spring Data JPA Repositories                │   │
│   └────────────────────────┬────────────────────────────┘   │
│                            ▼                                 │
│   ┌─────────────────────────────────────────────────────┐   │
│   │               JPA Entities                           │   │
│   └─────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │   MySQL 8    │
                    └──────────────┘
```

---

## Entidades y Relaciones

### Diagrama Simplificado

```
Perfil ──1:N──> Usuario ──1:N──> Pedido ──1:N──> DetallePedido
                   │                │                   │
                   │                │                   │
                   │                └──────N:1──> Mesa  │
                   │                │                   │
                   │                └──────N:1──> Cliente
                   │                                    │
Cliente ──1:N──> Reserva ──N:1──> Mesa                  │
    │                                                   │
    └──1:N──> Resena <──N:1── Producto <────────────────┘
                              │
                              └──N:1──> Categoria
```

### Estados de Pedido (Maquina de Estados)

```
PENDIENTE ──> EN_PREPARACION ──> SERVIDO ──> PAGADO
    │               │                │
    └───────────────┴────────────────┘
                    │
                    ▼
               CANCELADO
```

**Reglas:**
- `PENDIENTE` -> `EN_PREPARACION` -> `SERVIDO` -> `PAGADO`
- Desde cualquier estado (excepto `PAGADO`) se puede ir a `CANCELADO`
- `PAGADO` y `CANCELADO` son estados terminales

---

## Endpoints API

### Autenticacion (`/api/auth`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesion |
| POST | `/api/auth/registro` | Registrar cliente |
| POST | `/api/auth/logout` | Cerrar sesion |
| GET | `/api/auth/check-session` | Verificar sesion |
| GET | `/api/auth/current-user` | Usuario actual |

### Pedidos (`/pedido`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/pedido` | Listar todos |
| GET | `/pedido/{id}` | Obtener por ID |
| GET | `/pedido/usuario/{id}` | Por usuario |
| POST | `/pedido` | Crear |
| PUT | `/pedido/{id}` | Actualizar |
| DELETE | `/pedido/{id}` | Eliminar |

### Productos (`/producto`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/producto` | Listar todos |
| GET | `/producto/{id}` | Obtener por ID |
| POST | `/producto` | Crear (multipart) |
| PUT | `/producto/{id}` | Actualizar |
| DELETE | `/producto/{id}` | Eliminar |

### Otros Endpoints

- **Usuarios:** `/usuario`
- **Clientes:** `/cliente`
- **Mesas:** `/mesa`
- **Categorias:** `/categoria`
- **Reservas:** `/reserva`
- **Pagos:** `/pago`
- **Resenas:** `/resena`
- **Detalle Pedido:** `/detalle-pedido`

Todos siguen el patron CRUD estandar.

---

## Comandos Utiles

### Maven

```bash
# Ejecutar en desarrollo
./mvnw spring-boot:run

# Compilar sin tests
./mvnw clean package -DskipTests

# Compilar con tests
./mvnw clean package

# Ejecutar tests
./mvnw test

# Ejecutar test especifico
./mvnw test -Dtest=QtalLisuraApplicationTests

# Limpiar build
./mvnw clean
```

### Docker

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Ver logs
docker-compose logs -f

# Reiniciar
docker-compose restart

# Eliminar todo (incluyendo datos)
docker-compose down -v
```

### MySQL (Docker)

```bash
# Acceder a MySQL
docker exec -it qtallisura-mysql mysql -u root -proot dbrestaurant

# Backup
docker exec qtallisura-mysql mysqldump -u root -proot dbrestaurant > backup.sql

# Restaurar
docker exec -i qtallisura-mysql mysql -u root -proot dbrestaurant < backup.sql
```

---

## Configuracion

### application.properties

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/dbrestaurant
spring.datasource.username=root
spring.datasource.password=root

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Archivos
spring.servlet.multipart.max-file-size=5MB
file.upload-dir.users=uploads/users
file.upload-dir.products=uploads/products
```

### Variables de Entorno (Produccion)

Para produccion, considera usar variables de entorno:

```bash
export DB_URL=jdbc:mysql://servidor:3306/dbrestaurant
export DB_USERNAME=usuario
export DB_PASSWORD=password_seguro
```

Y en `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/dbrestaurant}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

---

## Solucion de Problemas

### Error: "Communications link failure"

**Causa:** MySQL no esta corriendo o no es accesible.

**Solucion:**
```bash
# Docker
docker-compose ps  # Verificar estado
docker-compose up -d  # Iniciar si esta detenido

# XAMPP
# Verificar que MySQL este iniciado en el panel de control
```

### Error: "Access denied for user 'root'@'localhost'"

**Causa:** Password incorrecto.

**Solucion:**
- Docker: El password es `root`
- XAMPP: Por defecto no tiene password. Actualizar `application.properties`:
  ```properties
  spring.datasource.password=
  ```

### Error: "Unknown database 'dbrestaurant'"

**Causa:** La base de datos no existe.

**Solucion:**
- Docker: Reiniciar con `docker-compose down && docker-compose up -d`
- XAMPP: Crear la base de datos manualmente (ver instrucciones arriba)

### Puerto 3306 en uso

**Causa:** Otro servicio usa el puerto.

**Solucion:**
```bash
# Encontrar el proceso
# Linux/Mac:
lsof -i :3306
# Windows:
netstat -ano | findstr :3306

# Matar el proceso o cambiar el puerto en docker-compose.yml
```

---

## Contacto y Soporte

Para reportar problemas o sugerencias, crear un issue en el repositorio.

---

**Q'Tal Lisura** - Sistema de Gestion de Restaurante
