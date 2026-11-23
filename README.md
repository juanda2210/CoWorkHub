# CoWorkHub

API REST para gestionar las **sedes, salas, miembros y reservas de un coworking**: disponibilidad de salas,
reservas con servicios adicionales y cobro calculado, cancelaciones con cargo, consumo mensual de horas
por plan, autenticación con JWT y permisos por rol.

Proyecto final del curso de Spring Boot (módulos 01 a 08).

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 + Spring Boot 4.1.2 | Aplicación y API REST |
| Spring Data JPA / Hibernate | Persistencia |
| MySQL 8.4 (Docker) | Base de datos |
| Spring Security + JWT (jjwt) | Autenticación sin sesiones, roles y permisos con `@PreAuthorize` |
| springdoc-openapi | Documentación con Swagger UI |
| Spring Boot Actuator | Salud del servicio (`/actuator/health`) |
| Lombok, Bean Validation | Menos código repetido y validación de los datos de entrada |
| Maven | Construcción del proyecto |

## Requisitos previos

- **JDK 21**
- **Docker** (para la base de datos) o un MySQL 8 instalado
- Opcional, para ejecutar las pruebas de aceptación: **Postman CLI** o la app de Postman

## 1. Crear la base de datos

### Con Docker

Elige una contraseña para MySQL y reemplaza `TU_CONTRASEÑA`:

```bash
docker run -d --name coworking-db \
  -e MYSQL_ROOT_PASSWORD=TU_CONTRASEÑA \
  -e MYSQL_DATABASE=coworking \
  -p 3308:3306 \
  -v coworking_data:/var/lib/mysql \
  mysql:8.4
```

La primera vez tarda unos segundos. Espera a que `docker logs coworking-db` muestre `ready for connections`.
El puerto `3308` del computador apunta al `3306` del contenedor, para no chocar con un MySQL instalado.

Comandos útiles:

| Para… | Comando |
|---|---|
| Ver si está corriendo | `docker ps` |
| Detenerlo | `docker stop coworking-db` |
| Volver a iniciarlo | `docker start coworking-db` |

### Con un MySQL instalado

Basta con que el servidor esté corriendo: la aplicación crea la base `coworking` si no existe. Si tu MySQL usa
el puerto `3306`, cambia `3308` por `3306` en `spring.datasource.url` de `src/main/resources/application.properties`.

## 2. Configurar la contraseña (archivo `.env`)

La contraseña de la base de datos **no está escrita en el código**: se lee de una variable llamada
`DB_PASSWORD`, que vive en un archivo `.env` que git ignora.

1. Copia el archivo de ejemplo:

   ```bash
   cp .env.example .env
   ```

2. Abre `.env` y escribe la contraseña que usaste en `MYSQL_ROOT_PASSWORD`:

   ```
   DB_PASSWORD=TU_CONTRASEÑA
   ```

   Sin espacios alrededor del `=` y sin comillas.

Si falta la variable, la aplicación no arranca y avisa que no puede resolver `DB_PASSWORD`.

Opcional: el secreto con el que se firman los JWT tiene un valor de desarrollo. Para usar otro, define la variable
de entorno `JWT_SECRET` (mínimo 32 caracteres) antes de arrancar.

## 3. Ejecutar el proyecto

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`.

> **Importante:** `spring.jpa.hibernate.ddl-auto=create` hace que **en cada arranque se borren y se vuelvan a crear
> las tablas**, y se carguen de nuevo los datos de `src/main/resources/import.sql`. Siempre empiezas con los
> mismos datos, pero lo que crees mientras la aplicación corre se pierde al reiniciarla.

## 4. Documentación (Swagger UI)

| Qué | Dirección |
|---|---|
| **Swagger UI** | <http://localhost:8080/doc/swagger-ui.html> |
| Especificación OpenAPI (JSON) | <http://localhost:8080/v3/api-docs> |
| Copia exportada de la especificación | [`docs/openapi.json`](docs/openapi.json) |

Los endpoints están organizados en cinco grupos (desplegable de la parte superior): Autenticación, Catálogos,
Miembros, Disponibilidad y Reservas. Cada endpoint indica el rol que puede usarlo, las reglas de negocio que
aplican y los errores posibles, con ejemplos en los cuerpos de entrada.

### Cómo probar un endpoint protegido en Swagger

1. Abre `POST /auth/login`, pulsa **Try it out**, escribe un usuario de prueba (ver abajo) y **Execute**.
2. Copia el valor de `token` de la respuesta.
3. Pulsa **Authorize** (arriba a la derecha), pega el token (sin la palabra `Bearer`) y confirma.
4. Ya puedes usar el resto de los endpoints. El token vence a los 60 minutos.

## 5. Usuarios de prueba

Cargados por `import.sql`. Son contraseñas de práctica para esta base de datos local.

| Usuario | Contraseña | Rol | Qué puede hacer |
|---|---|---|---|
| `admin` | `Admin123` | `ADMIN` | Todo; es el único que crea, modifica o elimina catálogos |
| `recep` | `Recep123` | `RECEPTIONIST` | Gestiona los miembros y todas las reservas |
| `ana` | `Member1` | `MEMBER` | Miembro Ana Gómez (plan Profesional); solo ve y gestiona **sus** reservas y su consumo |
| `carlos` | `Member2` | `MEMBER` | Miembro Carlos Ruiz (plan Básico); solo ve y gestiona **sus** reservas y su consumo |

### Datos iniciales (`import.sql`)

| Datos | Cantidad |
|---|---|
| Sedes | 2 (Centro: 08:00 a 20:00; Norte: 07:00 a 22:00) |
| Salas | 6, una inactiva, con su equipamiento |
| Equipamiento / servicios adicionales / planes | 4 / 3 / 3 |
| Miembros | 5, uno suspendido (Diego Torres) |
| Reservas | 15 en distintos estados, algunas con servicios |

Las fechas de las reservas se calculan a partir del día actual (`CURDATE()`), así que las reservas "futuras"
siempre lo son.

## 6. Endpoints principales

Base: `http://localhost:8080`. Salvo los públicos, todos exigen el encabezado `Authorization: Bearer <token>`.

| Recurso | Ruta | Acceso |
|---|---|---|
| Login | `POST /auth/login` | Público |
| Perfil | `GET /auth/perfil` | Autenticado |
| Salud | `GET /actuator/health` | Público |
| Sedes, salas, equipamientos, servicios adicionales, planes | `/api/sedes`, `/api/salas`, `/api/equipamientos`, `/api/servicios-adicionales`, `/api/planes` | Consulta: autenticado. Crear, actualizar y eliminar: `ADMIN` |
| Disponibilidad | `GET /api/disponibilidad?sedeId=&fecha=&horaInicio=&horaFin=` (+ `capacidadMinima`, `equipamientoIds`) | Autenticado |
| Miembros | `/api/miembros` (+ `/{id}/suspender`, `/{id}/activar`, `/{id}/consumo?mes=yyyy-MM`) | `ADMIN`, `RECEPTIONIST`; un `MEMBER` solo consulta lo suyo |
| Reservas | `/api/reservas` (+ `/{id}/servicios`, `/{id}/confirmar`, `/{id}/cancelar`, `/{id}/completar`) | `ADMIN`, `RECEPTIONIST`; un `MEMBER` solo opera sobre sus reservas (confirmar y completar son solo del personal) |

La lista completa, con cuerpos y respuestas, está en Swagger UI.

## 7. Reglas de negocio

| Regla | Resumen |
|---|---|
| RN-01 | Una sala no puede tener dos reservas `PENDING` o `CONFIRMED` que se crucen, ni siquiera con solicitudes simultáneas. Si una termina justo cuando empieza otra, no se cruzan |
| RN-02 | Duración de 30 minutos a 8 horas, en múltiplos de 30 minutos, dentro del mismo día |
| RN-03 | Dentro del horario de la sede, futura y con al menos 1 hora de anticipación |
| RN-04 | La sala debe estar activa y los asistentes no pueden superar su capacidad |
| RN-05 | Solo reserva un miembro `ACTIVE` |
| RN-06 | Un miembro no puede superar las reservas activas futuras que permite su plan |
| RN-07 | Las horas incluidas en el plan no se cobran; las excedentes valen `tarifa × (1 − descuento)`; los servicios suman `cantidad × precio` |
| RN-08 | Cancelación: 24 h o más, sin cargo; entre 24 h y 2 h, 50 % del costo de la sala; con menos de 2 h o ya iniciada, no se puede |
| RN-09 | Estados: `PENDING → CONFIRMED → COMPLETED`, o `PENDING/CONFIRMED → CANCELLED` |
| RN-10 | Los servicios de una reserva solo se cambian mientras está `PENDING` |
| RN-11 | Son únicos: documento, email y usuario del miembro; nombre de la sala dentro de su sede; nombre de equipamiento, servicio y plan |
| RN-12 | No se elimina nada que esté en uso (sede con salas, plan con miembros, sala o miembro con reservas, equipamiento o servicio usado) |
| RN-13 | Cada rol solo hace lo que le corresponde |

### Cómo se cumple la RN-01 con solicitudes simultáneas

Al crear una reserva, la sala se bloquea con un candado de base de datos (`SELECT ... FOR UPDATE`) como **primera
operación de la transacción**. El orden importa: MySQL toma la "foto" de lectura de la transacción en la primera
consulta normal, y si se toma antes de esperar el candado, la validación de cruces no vería la reserva de la
solicitud que ganó. Con el candado primero, la segunda solicitud espera y luego sí la ve, y responde `409`.

## 8. Errores

Toda respuesta de error (incluidos los `401` y `403` de la seguridad) tiene el mismo cuerpo:

```json
{
  "error": "La sala ya está reservada en ese horario",
  "codigo": "RN-01",
  "timestamp": "2030-06-03T13:00:00Z"
}
```

| Código HTTP | Cuándo | `codigo` |
|---|---|---|
| `400` | Datos faltantes o con formato inválido | `VALIDATION_ERROR` |
| `401` | Sin token, o con un token alterado o vencido | `UNAUTHORIZED` |
| `403` | El usuario no tiene permiso | `FORBIDDEN` |
| `404` | El recurso no existe | `NOT_FOUND` |
| `409` | Se viola una regla de negocio o hay un duplicado | La regla violada: `RN-01`, `RN-02`, … |

Las excepciones se lanzan en la capa de negocio (`NotFoundException`, `ConflictException`,
`InvalidDataException`) y un único `@RestControllerAdvice` las convierte en este cuerpo.

## 9. Pruebas de aceptación (Postman)

En [`docs`](docs) hay una colección de Postman con los **14 escenarios de aceptación**, con comprobaciones
automáticas (78 en total), y su entorno:

- [`docs/CoWorkHub.postman_collection.json`](docs/CoWorkHub.postman_collection.json)
- [`docs/CoWorkHub.postman_environment.json`](docs/CoWorkHub.postman_environment.json)

Con la aplicación corriendo y la base recién cargada (reinicia la aplicación antes de cada ejecución, porque las
pruebas crean y cancelan reservas):

```bash
postman collection run docs/CoWorkHub.postman_collection.json -e docs/CoWorkHub.postman_environment.json --env-var 'adminPassword=Admin123' --env-var 'recepPassword=Recep123' --env-var 'anaPassword=Member1' --env-var 'carlosPassword=Member2'
```

También puedes importar los dos archivos en la app de Postman y usar **Run collection**.

Resultado esperado: **29 peticiones y 78 comprobaciones, ninguna fallida**. Los escenarios 6 y 7 (cancelaciones
por cercanía de la hora) dependen de la hora a la que se ejecuten: si no hay una ventana válida, la prueba lo
indica como `OMITIDO`.

| # | Escenario | Resultado esperado |
|---|---|---|
| 1 | Reservar una sala libre con 2 servicios | `201` con el costo calculado |
| 2 | Reservar en un horario que se cruza | `409` `RN-01` |
| 3 | Reservar 10:00-11:00 con otra de 09:00-10:00 | `201` |
| 4 | Reservar 45 minutos o fuera del horario de la sede | `409` |
| 5 | Cancelar con más de 24 h de anticipación | `200`, cargo 0 |
| 6 | Cancelar entre 2 y 24 h | `200`, cargo del 50 % |
| 7 | Cancelar con menos de 2 h | `409` |
| 8 | Confirmar una reserva cancelada | `409` |
| 9 | Un miembro consulta la reserva de otro | `403` |
| 10 | Endpoint protegido sin token o con token alterado | `401` |
| 11 | Eliminar una sala con reservas | `409` |
| 12 | Crear una sede sin nombre | `400` |
| 13 | Dos solicitudes simultáneas para la misma sala y horario | Una `201` y la otra `409` |
| 14 | Listar miembros con `?estado=SUSPENDED` | `200`, solo los suspendidos |

## 10. Estructura del proyecto

El código sigue tres capas, y cada una solo conoce a la de abajo: `presentation → business → persistence`.

```
src/main/java/com/campuslands/coworkhub
├── presentation        Controladores, DTO (records) de entrada y salida, mapeadores y el manejador de errores
├── business            Interfaces de servicio, implementaciones, excepciones propias y cálculos de consumo
├── persistence         Entidades JPA, enumeraciones y repositorios
├── security            JWT, filtro de autenticación, usuario autenticado y reglas de dueño del recurso
└── config              Beans de configuración (Clock, PasswordEncoder y documentación OpenAPI)

src/main/resources      application.properties e import.sql (datos de prueba)
docs                    openapi.json y la colección de Postman
```

Algunas decisiones de diseño:

- **DTO como `record`**: nunca se devuelven entidades; los mapeadores convierten en el borde de la API.
- **`Clock` inyectado** en lugar de `LocalDateTime.now()`, para que las reglas que dependen de "ahora" (RN-03, RN-08)
  se puedan controlar.
- **Contraseñas con BCrypt**; el JWT es sin sesiones (`stateless`) y vence a los 60 minutos.
- **Carga por lotes de Hibernate** (`default_batch_fetch_size`) para evitar el problema de las N+1 consultas al
  leer relaciones `LAZY` en los listados.

## 11. Cambiar una contraseña de prueba

Las contraseñas se guardan como hash BCrypt en `import.sql`. Para cambiar una, genera el hash:

```bash
htpasswd -bnBC 10 "" 'LaNuevaContraseña' | tr -d ':\n' | sed 's/^\$2y/$2a/'
```

Copia el resultado (empieza por `$2a$10$` y tiene 60 caracteres, sin el `%` que a veces muestra la terminal al final),
reemplaza el hash del usuario correspondiente en `import.sql` y reinicia la aplicación.
