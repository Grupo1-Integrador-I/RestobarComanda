# 🍽️ RestoBar Comanda Digital

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Podman](https://img.shields.io/badge/Container-Podman-purple)](https://podman.io/)

Sistema web desarrollado en **Java 21** con **Spring Boot 3.2** para la gestión integral de comandas digitales en restobares, construido bajo **arquitectura hexagonal** (puertos y adaptadores) y principios **SOLID**, con persistencia en **PostgreSQL 16** contenerizada con **Podman**. Incluye autenticación segura con **BCrypt**, control de acceso por roles (Mesero, Preparador, Administrador), CRUD de mesas, productos y usuarios, toma de pedidos con carrito y cálculo automático del total, envío en tiempo real a cocina o barra mediante **WebSocket (STOMP)**, gestión del ciclo de vida del pedido, registro de pagos internos (efectivo/tarjeta) y liberación manual de mesas. El proyecto se encuentra en desarrollo activo: el dominio, los casos de uso y la infraestructura están implementados y funcionales, mientras que la capa de presentación (controladores REST y frontend HTML/CSS/JS) y la batería de pruebas unitarias se desarrollarán en las siguientes fases.

---

## ✨ Características Principales

- **Autenticación segura** con hash BCrypt y roles de usuario (Mesero, Preparador, Administrador).
- **CRUD completo** para mesas, productos y usuarios.
- **Toma de pedidos digital** con carrito, ajuste de cantidades y cálculo automático del total.
- **Envío en tiempo real** de pedidos a pantallas de cocina o barra mediante WebSocket (STOMP).
- **Enrutamiento automático** de ítems según categoría del producto (`COCINA` / `BARRA`).
- **Gestión del ciclo de vida** del pedido mediante máquina de estados controlada.
- **Registro de pago interno** (efectivo / tarjeta) sin integración con pasarelas.
- **Liberación manual de mesas** por parte del mesero, con validación de pedidos activos.
- **Bootstrap automático** del administrador inicial en el primer arranque.
- **Persistencia en PostgreSQL** con JPA e Hibernate.
- **Arquitectura hexagonal** con separación estricta entre dominio, aplicación e infraestructura.
- **Despliegue local con Podman** para el contenedor de PostgreSQL.
- **Manejo seguro de contraseñas** con `char[]`, hash BCrypt y limpieza de arrays sensibles.

---

## 🧱 Arquitectura del Proyecto

El sistema sigue el patrón **arquitectura hexagonal** (puertos y adaptadores), con separación estricta entre el núcleo del negocio y los detalles técnicos. Se aplican los principios **SOLID** en todas las capas.

**Regla de dependencia:** las capas externas dependen del núcleo, nunca al revés. El dominio no conoce Spring, JPA ni WebSocket.

### 🎯 Dominio (`dominio`)
- **Modelo** (`dominio.modelo`): Entidades puras del negocio (`Usuario`, `Mesero`, `Preparador`, `Administrador`, `Mesa`, `Producto`, `Pedido`, `ItemPedido`, `Pago`), Value Objects (`Monto`) y enums (`Rol`, `EstadoMesa`, `EstadoPedido`, `CategoriaProducto`, `TipoPago`).
- **Puertos** (`dominio.puerto`):
  - `repositorio`: Contratos de persistencia (`Repositorio`, `RepositorioUsuario`, `RepositorioMesa`, etc.).
  - `externo`: Contratos de servicios externos (`HashProvider`, `LoggerPort`, `NotificadorPedido`).
- **Reglas** (`dominio.reglas`): Lógica de dominio pura (`CalcularTotal`, `ValidadorPedido`, `ValidadorProducto`).

### ⚙️ Aplicación (`aplicacion`)
- **Casos de uso** (`aplicacion.casosdeuso`): Clases que orquestan el flujo de negocio. Ejemplos: `AutenticarUsuario`, `GestionUsuarios`, `GestionMesas`, `GestionProductos`, `TomarPedido`, `EnviarPedido`, `ActualizarEstadoPedido`, `EntregarPedido`, `CobrarPedido`, `LiberarMesa`, `BootstrapUseCase`.

### 🛠️ Infraestructura (`infraestructura`)
- **Persistencia** (`infraestructura.persistencia`):
  - `entidad`: Entidades JPA (`EntidadUsuario`, `EntidadMesa`, etc.).
  - `mapper`: Conversores entre dominio y entidades JPA.
  - `repositorioJPA`: Interfaces de Spring Data JPA.
  - `adaptador`: Implementaciones de los puertos del dominio.
- **Seguridad** (`infraestructura.seguridad`): `BCryptHashProvider`, `UsuarioDetails`, `UsuarioDetailsService`.
- **WebSocket** (`infraestructura.websocket`): Configuración STOMP y adaptador `NotificadorPedidoWebSocket`.
- **Logs** (`infraestructura.log`): Adaptador `Slf4jLoggerAdapter`.
- **Configuración** (`infraestructura.configuracion`): `SecurityConfig`, beans de Spring.

### 🖥️ Presentación (`presentacion`)
- Controladores REST y frontend estático (HTML/CSS/JS) — **pendiente de desarrollo**.

### 🚀 Arranque (`boot`)
- `RestobarApp`: Clase principal con `@SpringBootApplication`.
- `BootstrapRunner`: Inicializa el administrador en el primer arranque.

---

## 📁 Estructura de Paquetes

```
RestoBar_Int1/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── dominio/                    # 🎯 Núcleo del negocio
    │   │   │   ├── modelo/                 #   Usuario, Mesero, Preparador, Administrador,
    │   │   │   │                           #   Mesa, Producto, Pedido, ItemPedido, Pago,
    │   │   │   │                           #   Monto, Rol, EstadoMesa, EstadoPedido,
    │   │   │   │                           #   CategoriaProducto, TipoPago
    │   │   │   ├── puerto/
    │   │   │   │   ├── externo/            #   HashProvider, LoggerPort, NotificadorPedido
    │   │   │   │   └── repositorio/        #   Repositorio<T> + 5 contratos específicos
    │   │   │   └── reglas/                 #   CalcularTotal, ValidadorPedido, ValidadorProducto
    │   │   │
    │   │   ├── aplicacion/                 # ⚙️ Casos de uso
    │   │   │   └── casosdeuso/             #   AutenticarUsuario, BootstrapUseCase,
    │   │   │                               #   GestionUsuarios, GestionMesas, GestionProductos,
    │   │   │                               #   TomarPedido, EnviarPedido, ActualizarEstadoPedido,
    │   │   │                               #   EntregarPedido, CobrarPedido, LiberarMesa
    │   │   │
    │   │   ├── infraestructura/            # 🛠️ Adaptadores
    │   │   │   ├── configuracion/          #   SecurityConfig
    │   │   │   ├── log/                    #   Slf4jLoggerAdapter
    │   │   │   ├── persistencia/
    │   │   │   │   ├── adaptador/          #   5 adaptadores de repositorio
    │   │   │   │   ├── entidad/            #   6 entidades JPA
    │   │   │   │   ├── mapper/             #   6 mappers dominio ↔ JPA
    │   │   │   │   └── repositorioJPA/     #   5 interfaces Spring Data
    │   │   │   ├── seguridad/              #   BCryptHashProvider, UsuarioDetails,
    │   │   │   │                           #   UsuarioDetailsService, UtilLimpieza
    │   │   │   └── websocket/              #   WebSocketConfig, NotificadorPedidoWebSocket,
    │   │   │                               #   MensajePedido (DTO)
    │   │   │
    │   │   ├── presentacion/               # 🖥️ Controladores REST y frontend
    │   │   └── boot/                       # 🚀 RestobarApp, BootstrapRunner
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/                           # 🧪 Pruebas unitarias (pendiente)
```

---

## 📊 Modelo de Dominio

El siguiente resumen describe las principales entidades del negocio y sus relaciones:

| Entidad | Atributos clave | Relaciones |
|---------|-----------------|------------|
| **Usuario** | código, hashContraseña, DNI, nombre, apellido, rol | Clase base abstracta. Heredan `Mesero`, `Preparador`, `Administrador`. |
| **Mesa** | número, ubicación, estado | Una mesa puede tener muchos pedidos activos. |
| **Producto** | nombre, precio (`Monto`), categoría, disponible | Categoría `COCINA` o `BARRA`. |
| **Pedido** | mesa, mesero, ítems, total (`Monto`), estado, fecha | Máquina de estados controlada. |
| **ItemPedido** | producto, cantidad, subtotal (`Monto`) | Pertenece a un único pedido. |
| **Pago** | pedido, monto, tipo, fecha | Un pedido tiene un único pago. |
| **Monto** | valor (`BigDecimal`) | Value Object con escala fija de 2 decimales y redondeo HALF_UP. |

### 🎭 Roles del sistema

| Rol | Descripción |
|-----|-------------|
| **MESERO** | Toma pedidos, gestiona mesas, registra pagos. |
| **PREPARADOR** | Visualiza pedidos en tiempo real y actualiza estados de preparación (cocina y barra). |
| **ADMINISTRADOR** | Gestiona mesas, productos y usuarios. |

---

## 🔄 Flujo del Pedido

El ciclo de vida del pedido está controlado por una máquina de estados que garantiza que solo se permitan transiciones válidas:

```
CREADO ──(mesero envía)──> ENVIADO
ENVIADO ──(preparador toma)──> EN_PREPARACION
EN_PREPARACION ──(preparador termina)──> LISTO
LISTO ──(mesero entrega)──> ENTREGADO
ENTREGADO ──(mesero cobra)──> PAGADO
```

En cualquier estado previo a `PAGADO`, el mesero puede **cancelar** el pedido.

### 📡 Canales WebSocket

| Tópico | Suscriptores | Contenido |
|--------|--------------|-----------|
| `/topic/cocina` | Pantallas de cocina | Pedidos con ítems de categoría `COCINA`. |
| `/topic/barra` | Pantallas de barra | Pedidos con ítems de categoría `BARRA`. |
| `/topic/meseros` | Pantallas de meseros | Cambios de estado de pedidos. |

**Endpoint de conexión:** `ws://localhost:8080/ws`

---

## 🚀 Instalación y Ejecución

### Requisitos previos

- **Java 21** → `java --version`
- **Maven 3.9+** → `mvn --version`
- **Podman** → `podman --version`
- **Git**

### 1. Clona el repositorio

```bash
git clone git@github.com:Grupo1-Integrador-I/restobar-comanda.git
cd restobar-comanda
```

### 2. Levanta PostgreSQL con Podman

```bash
podman run -d \
  --name restobar-db \
  -e POSTGRES_USER=restobar \
  -e POSTGRES_PASSWORD=restobar \
  -e POSTGRES_DB=restobar \
  -p 5432:5432 \
  -v restobar-db-data:/var/lib/postgresql/data \
  docker.io/library/postgres:16
```

Verifica que está corriendo:

```bash
podman ps
```

**Comandos útiles:**

| Acción | Comando |
|--------|---------|
| Detener | `podman stop restobar-db` |
| Iniciar | `podman start restobar-db` |
| Ver logs | `podman logs restobar-db` |
| Eliminar (conserva datos) | `podman rm restobar-db` |

### 3. Arranca la aplicación

```bash
mvn clean spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

### 4. Verifica la base de datos

```bash
podman exec -it restobar-db psql -U restobar -d restobar -c "\dt"
```

Deben aparecer **6 tablas**: `usuarios`, `mesas`, `productos`, `pedidos`, `items_pedido`, `pagos`.

---

## 🔑 Credenciales de Prueba

En el primer arranque, el `BootstrapRunner` crea automáticamente el administrador inicial:

| Rol | Código | Contraseña |
|-----|--------|------------|
| Administrador | `admin` | `admin123` |

> ⚠️ **Importante:** El sistema permite que el Administrador cree cuentas para Meseros y Preparadores desde el panel de gestión de usuarios. No hay cuentas predefinidas para estos roles.

### ¿Cómo probar otros roles?

1. Inicia sesión como **Administrador** con las credenciales de arriba.
2. Desde el panel de administración, accede al módulo de **gestión de usuarios**.
3. Crea cuentas para Mesero y/o Preparador completando los datos requeridos.
4. Cierra sesión y prueba con las nuevas credenciales.

> ⚠️ **Nota:** cambiar la contraseña por defecto del administrador antes de cualquier despliegue en producción.

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Descripción |
|------------|-------------|
| Java 21 | Lenguaje de programación principal. |
| Spring Boot 3.2 | Framework backend (MVC, Data JPA, Security, WebSocket). |
| Spring Data JPA | Persistencia y abstracción de repositorios. |
| Spring Security | Autenticación con sesiones y roles. |
| WebSocket (STOMP) | Comunicación en tiempo real. |
| PostgreSQL 16 | Base de datos relacional. |
| HTML5 / CSS3 / JS | Frontend estático (pendiente). |
| Maven | Gestión de dependencias y ciclo de vida. |
| JUnit 5 + Mockito | Pruebas unitarias (pendiente). |
| SLF4J + Logback | Sistema de logs. |
| Podman | Contenerización de PostgreSQL. |
| Git / GitHub | Control de versiones y repositorio remoto. |

---

## 📦 Configuración

Variables de entorno opcionales (con valores por defecto en `application.properties`):

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/restobar` |
| `DB_USER` | Usuario de base de datos | `restobar` |
| `DB_PASSWORD` | Contraseña de base de datos | `restobar` |
| `ADMIN_CODIGO` | Código del admin inicial | `admin` |
| `ADMIN_CONTRASENA` | Contraseña del admin inicial | `admin123` |
| `ADMIN_DNI` | DNI del admin | `00000000` |
| `ADMIN_NOMBRE` | Nombre del admin | `Admin` |
| `ADMIN_APELLIDO` | Apellido del admin | `Principal` |

---

## 🤝 Cómo Contribuir

Las contribuciones son bienvenidas. Si deseas colaborar:

1. Haz un **fork** del repositorio.
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m 'Agrega nueva funcionalidad'`).
4. Sube la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un **Pull Request** describiendo tus cambios.

Para reportar errores o sugerir mejoras, por favor abre un **Issue** en el repositorio.

### Convenciones del proyecto

- **Arquitectura:** respetar la regla de dependencia hexagonal. El dominio no conoce frameworks.
- **Nombres:** clases en PascalCase, métodos y variables en camelCase.
- **Archivos:** el nombre del archivo `.java` debe coincidir con la clase pública.
- **Sangría:** 4 espacios. Configurar `format on save` en el IDE.
- **Commits:** mensajes claros en imperativo.

### Antes de cada commit

```bash
mvn clean compile    # Debe compilar sin errores
mvn test             # Las pruebas deben pasar
```

---

## 📄 Licencia

Este proyecto está bajo la **GNU General Public License v3.0**. Ver [LICENSE](LICENSE) para más detalles.
