# CoffeeStation · API de la cafetería

API REST para la cafetería CoffeeStation de SafaReyes (Caso 1). Gestiona la carta (productos, categorías y alérgenos), los pedidos en barra, los cupones de descuento y los informes de ventas.

**Tecnologías:** Java 25, Spring Boot 4, Spring Data JPA, Spring Security con JWT, MapStruct, Lombok y PostgreSQL.

## Requisitos

- JDK 25
- PostgreSQL 16 en marcha (por defecto en `localhost:5432`)
- No hace falta instalar Maven: el proyecto trae el wrapper `./mvnw`

## Puesta en marcha

### 1. Variables de entorno

Copia la plantilla y rellena tus valores:

```bash
cp .env
```

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de PostgreSQL, p. ej. `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USER` / `DB_PASSWORD` | Credenciales de la base de datos |
| `DB_SCHEMA` | Esquema donde están las tablas (`cafeteria`) |
| `JWT_SECRETO` | Secreto para firmar los tokens; al menos 32 caracteres |
| `SAFAREYES_COFFEESTATION_API_KEY` | Clave que las otras API deben enviar en la cabecera `X-API-KEY` para usar `/api/v1/integracion/**` (sin ella, 401) |

El `.env` no se sube a git. La aplicación lo lee al arrancar (`spring.config.import` en `application.properties`).

### 2. Base de datos

El script `db/cafeteria.sql` crea los tipos, las tablas y los datos de ejemplo. **Borra y recrea todo**, así que solo debe ejecutarse para empezar de cero:

```bash
psql -h localhost -U postgres -d postgres -c "CREATE SCHEMA IF NOT EXISTS cafeteria;"
PGOPTIONS="-c search_path=cafeteria" psql -h localhost -U postgres -d postgres \
     -v ON_ERROR_STOP=1 -1 -f db/cafeteria.sql
```

Hibernate funciona con `ddl-auto=validate`: no crea tablas, solo comprueba que las entidades coinciden con el esquema.

### 3. Arrancar

```bash
./mvnw spring-boot:run
```

La API queda en **http://localhost:8081/api/v1**.

### 4. Tests

```bash
./mvnw test
```

Los tests de repositorio (`@DataJpaTest`) se ejecutan contra la base de datos real con los datos del script, así que hay que cargarlo antes. Cada test se deshace al terminar y no deja cambios en la BD.

## Usuarios de prueba

| Rol | Email | Contraseña |
|---|---|---|
| ADMIN | `gerencia@cafeteria.com` | `admin123` |
| BARISTA | `marcos.barista@cafeteria.com` | `barista123` |
| CLIENTE | `carlos.gomez@gmail.com` | `cliente123` |

Login:

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"gerencia@cafeteria.com","password":"admin123"}'
```

La respuesta trae un `token`, que se envía en la cabecera `Authorization: Bearer <token>` en el resto de peticiones. Los clientes nuevos se registran con `POST /api/v1/auth/registro`.

## Endpoints

| # | Método | Ruta | Roles |
|---|---|---|---|
| 1 | GET | `/productos` — carta paginada con filtros (`categoriaId`, `nombre`, `precioMaximo`, `disponible`, `sinAlergenoId`) | Público |
| 2 | GET | `/productos/{id}` — detalle con alérgenos | Público |
| 3 | POST | `/productos` | ADMIN |
| 4 | PUT | `/productos/{id}` | ADMIN |
| 5 | PATCH | `/productos/{id}/disponibilidad` | ADMIN, BARISTA |
| 6 | DELETE | `/productos/{id}` — baja lógica | ADMIN |
| 7 | GET | `/categorias` — con número de productos activos | Público |
| 8 | PATCH | `/categorias/{id}/disponibilidad` — agotar o reponer la categoría | ADMIN, BARISTA |
| 9 | POST | `/pedidos` — con `codigoCupon` o `dniSocio` opcionales | BARISTA, CLIENTE |
| 10 | GET | `/pedidos/{id}` | ADMIN, BARISTA, CLIENTE (propio) |
| 11 | GET | `/pedidos` — paginado por `estado`, `desde` y `hasta` | ADMIN, BARISTA, CLIENTE (solo los suyos) |
| 12 | PATCH | `/pedidos/{id}/estado` | ADMIN, BARISTA |
| 13 | POST | `/cupones` | ADMIN |
| 14 | POST | `/cupones/validar` | Autenticado |
| 15 | GET | `/informes/ventas-dia?fecha=` | ADMIN |
| 16 | GET | `/informes/productos-top?desde=&hasta=&limite=` | ADMIN |

Todas las rutas van precedidas de `/api/v1`. Los listados paginados admiten `page`, `size` y `sort` (p. ej. `?page=0&size=10&sort=precioVenta,asc`).

Los errores se devuelven en formato `ProblemDetail`. Los códigos son:

- **400**: datos o parámetros no válidos.
- **401**: falta el token o no es válido.
- **403**: el rol no tiene permiso.
- **404**: el recurso no existe.
- **409**: el recurso está duplicado.
- **422**: se ha incumplido una regla de negocio.

## Consultas (Q1–Q8)

17 consultas propias, todas usadas desde algún servicio.

| Formato | Consulta | Repositorio | Endpoint que la usa |
|---|---|---|---|
| Q1 Derivada | `findByCodigoIgnoreCase` | `ICuponRepositorio` | 9 crear pedido (cupón), 14 validar cupón |
| Q1 Derivada | `existsByCodigoIgnoreCase` | `ICuponRepositorio` | 13 crear cupón (código repetido → 409) |
| Q1 Derivada | `findByUsuario_IdUsuario` | `IClienteRepositorio` | 9, 10, 11 (pedidos propios del CLIENTE) |
| Q1 Derivada | `findByMailIgnoreCase` | `IUsuarioRepositorio` | login y filtro JWT |
| Q1 Derivada | `existsByMailIgnoreCase` | `IUsuarioRepositorio` | registro (email repetido → 409) |
| Q1 Derivada | `countByFechaGreaterThanEqual` | `IPedidoRepositorio` | 9 crear pedido (número de turno, RN-09) |
| Q1 Derivada | `countByCupon_IdCuponAndEstadoNot` | `IPedidoRepositorio` | 9 y 14 (usos del cupón sin contar cancelados, RN-06) |
| Q1 Derivada | `findByIdProductoAndActivaTrue` | `IProductoRepositorio` | 2 detalle, 5 disponibilidad, 6 baja (RN-01) |
| Q2 JPQL | `buscarCategoriasProductosActivos` (LEFT JOIN + COUNT + GROUP BY) | `ICategoriaRepositorio` | 7 categorías |
| Q2 JPQL | `buscarPedidos` | `IPedidoRepositorio` | 11 listado de pedidos |
| Q2 JPQL | `buscarPedidosCliente` | `IPedidoRepositorio` | 11 listado de pedidos de un cliente |
| Q3 Nativa | `obtenerProductosTop` (DATE_TRUNC + LIMIT) | `IProductoRepositorio` | 16 productos más vendidos |
| Q3 Nativa | `obtenerVentasDia` (DATE_TRUNC + COALESCE) | `IPedidoRepositorio` | 15 ventas del día |
| Q4 Modificación | `cambiarDisponibilidadCategoria` (`@Modifying` UPDATE masivo) | `IProductoRepositorio` | 8 agotar/reponer categoría |
| Q5 Proyección | interfaz `ProductoCartaVista` (con `findBy(...).as(...)`) | `IProductoRepositorio` | 1 carta |
| Q5 Proyección | record `CategoriasActivos` con `SELECT new` | `ICategoriaRepositorio` | 7 categorías |
| Q6 Paginación | `Pageable` → `Page<…>` en `buscarPedidos`, `buscarPedidosCliente` y la carta | `IPedidoRepositorio`, `IProductoRepositorio` | 1 carta, 11 pedidos |
| Q7 Specification | `findBy(ProductoSpecifications.carta(...))` | `IProductoRepositorio` | 1 carta con filtros combinables |
| Q8 N+1 | `findById` con `@EntityGraph` (líneas, productos y cupón) | `IPedidoRepositorio` | 10 detalle del pedido |
| Q8 N+1 | `findById` con `@EntityGraph` (categoría y alérgenos) | `IProductoRepositorio` | 4 modificar producto |

## Postman

La colección `postman/cafeteria.postman_collection.json` tiene peticiones de prueba para todos los endpoints, con tests automáticos. Depende de los datos de `db/cafeteria.sql`.

## Estructura

```
src/main/java/es/safareyes/cafeteria/
├── controladores/   endpoints REST y manejador global de errores
├── servicios/       reglas de negocio
├── repositorios/    consultas JPA y Specifications de la carta
├── modelos/         entidades y enums
├── dtos/
│   ├── request/     DTOs de entrada (con sus validaciones)
│   └── response/    DTOs de salida y proyecciones
├── validaciones/    validador propio @DniValido
├── conversores/     mappers MapStruct
├── excepciones/     excepciones propias
├── security/        Spring Security: JWT y filtro X-API-KEY de /api/v1/integracion/**
└── integracion/     cliente de FitZone
db/cafeteria.sql     esquema y datos de ejemplo
```
