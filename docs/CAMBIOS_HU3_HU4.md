# Cambios HU3 + HU4 (autenticación, autorización y revisión de solicitudes)

## Alcance y decisiones

- Se conserva el stack del scaffold: **Spring MVC, Spring Data JPA, Java 25, Gradle 9.8.2, PostgreSQL y JWT**.
- **WebFlux queda explícitamente fuera de alcance**: no se agregaron `Mono`, `Flux` ni transacciones reactivas.
- Los datos del cliente para la revisión se guardan como **snapshot en la solicitud**; no hay llamadas REST síncronas del microservicio de solicitudes al de autenticación.
- El primer `ADMIN` se crea mediante **bootstrap por variables de entorno**, deshabilitado por defecto.
- Cada historia se entrega en su propia rama `feature`:
  - `feature/HU3-autenticacion-autorizacion`
  - `feature/HU4-listado-solicitudes` (creada desde HU3)

## 1. Jerarquía de roles (HU3)

La política vive en el dominio (`RolUsuario#puedeRegistrar`) y se aplica además en el adaptador REST.

| Solicitante autenticado | Puede crear |
|---|---|
| `ADMIN` | `ADMIN`, `ASESOR`, `CLIENTE` |
| `ASESOR` | `CLIENTE` |
| `CLIENTE` | Nadie (registro público aparte) |

- `POST /api/v1/usuarios` sigue siendo público y **siempre** registra `CLIENTE`.
- `POST /api/v1/usuarios/internos?rol=...` requiere `ADMIN` o `ASESOR` y recibe el rol del solicitante desde el JWT (`@AuthenticationPrincipal`). Ya no es posible invocarlo indicando sólo el rol objetivo.
- Si el rol autenticado no puede crear el rol objetivo, el dominio lanza `RolNoPermitidoException` y la API responde **403**.
- El JWT incluye `sub` (id), `email`, `role`, issuer `crediya-auth` y expiración configurable.

### Bootstrap del primer ADMIN

Variables (ver `application.yaml` de autenticación):

| Variable | Default | Observación |
|---|---|---|
| `BOOTSTRAP_ADMIN_ENABLED` | `false` | Debe ponerse en `true` explícitamente |
| `BOOTSTRAP_ADMIN_PASSWORD` | vacío | Obligatoria si se habilita; usar un secreto |
| `BOOTSTRAP_ADMIN_CORREO` | `admin@crediya.com` | |
| `BOOTSTRAP_ADMIN_DOCUMENTO` | `0000000001` | |
| `BOOTSTRAP_ADMIN_NOMBRES` / `APELLIDOS` | `Administrador` / `CreDiYa` | |
| `BOOTSTRAP_ADMIN_FECHA_NACIMIENTO` | `1990-01-01` | |
| `BOOTSTRAP_ADMIN_DIRECCION` / `TELEFONO` | `Sin dirección` / `3000000000` | |
| `BOOTSTRAP_ADMIN_SALARIO_BASE` | `1000000` | Debe cumplir las reglas de registro |

El proceso es idempotente: si ya existen el correo o el documento, no hace nada. Se ejecuta en `ApplicationReadyEvent` y nunca registra la contraseña en logs.

## 2. Contrato HTTP unificado (corrección)

Ambos servicios responden los errores con la misma forma:

```json
{
  "timestamp": "2026-11-18T17:00:00Z",
  "status": 400,
  "message": "La solicitud contiene datos inválidos.",
  "details": { "monto": "El monto debe ser mayor que cero." }
}
```

Esto aplica también a errores de seguridad (401/403) y de token inválido.

## 3. Cambios de contrato en solicitudes (HU2 + HU4)

### `POST /api/v1/solicitud`

- **Eliminado del body**: `solicitanteId`. Ahora se deriva del JWT, evitando que un cliente radique solicitudes a nombre de otro.
- **Eliminado del body**: `correoElectronico`; se toma del claim `email` del JWT.
- **Agregados al body**: `nombreCliente`, `salarioBase`.
- La solicitud se persiste con el snapshot del cliente y con `estado = PENDIENTE_REVISION`.
- Se calcula y persiste `tasaInteres` (según tipo de préstamo) y `cuotaMensual` (amortización francesa).

Ejemplo:

```json
{
  "documentoCliente": "1020304050",
  "nombreCliente": "Ana María Gómez",
  "salarioBase": 2500000,
  "monto": 5000000,
  "plazoMeses": 24,
  "codigoTipoPrestamo": "LIBRE_INVERSION"
}
```

### `GET /api/v1/solicitud` (nuevo, HU4)

Solo `ASESOR` y `ADMIN`. Parámetros opcionales: `estado`, `codigoTipoPrestamo`, `documentoCliente`; paginación `page` (default 0) y `size` (default 20, máximo 100).

Respuesta:

```json
{
  "content": [{
    "id": 7,
    "solicitanteId": 15,
    "documentoCliente": "1020304050",
    "nombreCliente": "Ana María Gómez",
    "correoElectronico": "ana@example.com",
    "salarioBase": 2500000,
    "monto": 5000000,
    "plazoMeses": 24,
    "codigoTipoPrestamo": "LIBRE_INVERSION",
    "nombreTipoPrestamo": "Crédito de libre inversión",
    "tasaInteres": 0.019,
    "estado": "PENDIENTE_REVISION",
    "deudaTotalMensualSolicitudesAprobadas": 350000
  }],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

La deuda mensual se calcula sumando `cuotaMensual` de las solicitudes del mismo solicitante en estado `APROBADA`.

## 4. Cambios de esquema y migración

- `tipos_prestamo.tasa_interes DECIMAL(9,6) NOT NULL` (nuevo). Valores iniciales: `LIBRE_INVERSION` 1.9% mensual, `LIBRANZA` 1.5%, `VIVIENDA` 1.1%.
- `solicitudes`: nuevas columnas `nombre_cliente`, `correo_electronico`, `salario_base`, `tasa_interes`, `cuota_mensual`.
- `solicitudes.estado` pasa de texto descriptivo (`"Pendiente de revisión"`) a **enum persistido como string** (`PENDIENTE_REVISION`, `REVISION_MANUAL`, `APROBADA`, `RECHAZADA`).
- `data.sql` migra los valores antiguos de `estado` y rellena snapshot/tasas faltantes.

**Impacto**: el body de `POST /api/v1/solicitud` cambia (ver arriba) y el campo `estado` de las respuestas ahora es el nombre del enum, no su descripción. Las bases existentes deben ejecutar la migración incluida antes de desplegar la nueva versión.

## 5. Otras correcciones

- Eliminada la duplicación del bloque `security:` en `application.yaml` de solicitudes.
- CORS de autenticación deja el `TODO`: métodos permitidos explícitos `GET`, `POST`, `OPTIONS`.
- Dockerfile de ambos servicios: se crea el usuario `appuser` antes de usarlo y se elimina `-Xshareclasses` (opción de OpenJ9 incompatible con el JDK Temurin usado en la imagen).
- Configuración de PIT: se excluyen los tests de contexto Spring/Mockito que no son estables en el entorno de mutación (fallaban desde la línea base, antes de estos cambios). Los tests unitarios y de mutación del resto pasan.

## Notas de arranque (problemas encontrados al ejecutar en local)

Al levantar los servicios con `bootRun` (no cubiertos por los tests de contexto) aparecieron tres fallos que ya están corregidos:

1. **Falta de bean `ObjectMapper`**: Spring Boot 4 autoconfigura un `ObjectMapper` de Jackson 3 (`tools.jackson.*`), pero los filtros JWT inyectan Jackson 2 (`com.fasterxml.jackson.*`). Se agregó `JacksonConfig` en `api-rest` de ambos servicios, con un bean `jackson2ObjectMapper` (nombre distinto para no chocar con el `ObjectMapperConfig` de `org.reactivecommons`).
2. **Falta de bean `ValidarPlazoSolicitud`**: el `ComponentScan` de `UseCasesConfig` sólo incluye clases que terminan en `UseCase`. Se registró explícitamente como `@Bean`.
3. **Columnas `NOT NULL` sobre tablas con datos**: `ddl-auto: update` no podía agregar las columnas nuevas del snapshot porque `solicitudes` y `tipos_prestamo` ya tenían filas. Se agregó `@ColumnDefault` en las entidades para que Hibernate genere `DEFAULT` en el `ALTER TABLE`. `data.sql` luego actualiza los valores reales (las tasas se fuerzan siempre, no sólo cuando son NULL, porque el default dejó 0).
4. **`@RequestParam` sin nombre explícito**: sin el flag `-parameters` en tiempo de compilación, Spring no podía resolver los parámetros `rol`, `estado`, `codigoTipoPrestamo`, `documentoCliente`, `page` y `size` (HTTP 400). Se agregó `name = "..."` en todas las anotaciones.
5. **`UPPER(:parametro)` en Hibernate 7 + PostgreSQL**: el filtro por código de tipo de préstamo generaba `upper(?)` y PostgreSQL infería el parámetro como `bytea` (`function upper(bytea) does not exist`). Se normaliza el valor a mayúsculas en el adapter y la consulta compara contra el parámetro directamente.
6. **PowerShell y `curl.exe`**: al pasar JSON con comillas dobles desde Windows PowerShell, `curl.exe` puede perderlas y provocar un 400 engañoso. Para probar en local es preferible `Invoke-RestMethod` con `-Body (@{...} | ConvertTo-Json)`.

## Verificación

```powershell
cd crediya-autenticacion_ms; .\gradlew.bat build --console=plain
cd ..\crediya-solicitudes_ms; .\gradlew.bat build --console=plain
```

Ambos `build` terminan en `BUILD SUCCESSFUL`, incluyendo PIT.
