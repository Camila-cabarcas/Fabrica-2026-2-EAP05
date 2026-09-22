# Historias de Usuario Implementadas - Sprint-1

Documentación basada en los endpoints reales implementados en el código (`AuthController.java`, `UsuarioController.java`). No incluye historias de reset password ni ninguna otra funcionalidad no implementada en este sprint.

---

## HU01: Iniciar Sesión (Login)

**Endpoint:** `POST /api/auth/login`

**Controller:** `AuthController.login()`

Valida las credenciales del usuario contra Supabase Auth y, si son correctas, retorna un JWT propio de la aplicación para acceder a rutas protegidas.

### Request

```json
{
  "email": "cliente@example.com",
  "contrasena": "MiPassword123"
}
```

### Response exitosa — 200 OK

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "mensaje": "Inicio de sesión exitoso",
  "usuario": "cliente@example.com",
  "rol": "cliente"
}
```

### Errores

| Status | Causa |
|---|---|
| 400 | Body inválido (`email` o `contrasena` en blanco / email mal formado) |
| 401 | Credenciales inválidas, usuario no encontrado, o usuario inactivo |

### Criterios de aceptación

- [ ] El endpoint es público (no requiere JWT previo).
- [ ] Rechaza credenciales incorrectas con 401, sin filtrar si el email existe o no.
- [ ] Rechaza usuarios con `activo = false` aunque la contraseña sea correcta.
- [ ] El JWT retornado incluye el rol del usuario como claim.

---

## HU03: Cerrar Sesión (Logout)

**Endpoint:** `POST /api/auth/logout`

**Controller:** `AuthController.logout()`

Invalida la sesión actual agregando el JWT a una blacklist local. **No** hace ninguna llamada a Supabase — la revocación es enteramente local (`TokenBlacklistService`), y separado de eso se cierra el tracking de inactividad (`InactivityTrackingService`).

### Request

Sin body. Header opcional:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response exitosa — 200 OK

```
Sesión cerrada exitosamente
```

*(Texto plano, no JSON.)*

### Errores

| Status | Causa |
|---|---|
| 401 | No hay sesión activa (sin JWT válido en el contexto de seguridad) |

### Criterios de aceptación

- [ ] Un token ya usado en logout queda invalidado para requests posteriores (verificado por `JwtFilter` contra `TokenBlacklistService`).
- [ ] Sin un JWT válido, responde 401 en vez de 200.
- [ ] No depende de disponibilidad de Supabase para funcionar.

---

## HU04: Registrar Cliente

**Endpoint:** `POST /api/usuario/registrar`

**Controller:** `UsuarioController.registrarCliente()`

Crea un nuevo usuario con rol `cliente`. Primero crea el usuario en Supabase Auth (`auth.users`, sin contraseña) y luego el perfil en `public.usuario`, usando el mismo UUID en ambas tablas para satisfacer el FK entre ellas.

### Request

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 10 #20-30"
}
```

### Response exitosa — 201 Created

```json
{
  "id": "072d771c-11cb-4ff8-bd92-79e242e7512b",
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 10 #20-30",
  "rol": "cliente",
  "createdAt": "2026-09-21T10:15:00-05:00"
}
```

### Errores

| Status | Causa |
|---|---|
| 400 | Campos obligatorios en blanco o email mal formado |
| 409 | El email ya está registrado |

### Criterios de aceptación

- [ ] El endpoint es público (no requiere JWT).
- [ ] El `id` retornado existe simultáneamente en `auth.users` y `public.usuario`.
- [ ] El rol queda forzado a `cliente`, sin importar qué envíe el cliente en el body (el DTO no acepta `rol`).
- [ ] Un email duplicado no deja usuarios huérfanos en `auth.users` (rollback vía `SupabaseAuthService.eliminarUsuarioAuth`).

---

## HU05: Registrar Proveedor

**Endpoint:** `POST /api/usuario/registrar/proveedor`

**Controller:** `UsuarioController.registrarProveedor()`

Idéntico a HU04, pero protegido: solo un administrador autenticado puede crear proveedores, y el rol queda forzado a `proveedor`.

### Autenticación requerida

```
Authorization: Bearer <token_de_administrador>
```

Requiere rol `administrador` (`@PreAuthorize("hasRole('administrador')")`).

### Request

```json
{
  "nombre": "Distribuidora ABC",
  "email": "contacto@abc.com",
  "telefono": "3009876543",
  "direccion": "Cra 50 #12-40"
}
```

### Response exitosa — 201 Created

```json
{
  "id": "9a1c2e3f-4b5d-6e7f-8a9b-0c1d2e3f4a5b",
  "nombre": "Distribuidora ABC",
  "email": "contacto@abc.com",
  "telefono": "3009876543",
  "direccion": "Cra 50 #12-40",
  "rol": "proveedor",
  "createdAt": "2026-09-21T10:20:00-05:00"
}
```

### Errores

| Status | Causa |
|---|---|
| 400 | Campos obligatorios en blanco o email mal formado |
| 401 | Sin JWT válido |
| 403 | JWT válido pero sin rol `administrador` |
| 409 | El email ya está registrado |

### Criterios de aceptación

- [ ] Un usuario con rol `cliente` intentando este endpoint recibe 403, no 201.
- [ ] Sin ningún JWT, recibe 401, no 403 (el `AuthorizationFilter` distingue "no autenticado" de "autenticado sin permiso").
- [ ] El rol queda forzado a `proveedor`, igual que HU04 fuerza `cliente`.

---

## Resumen - Sprint-1

| HU | Feature | Endpoint | Método | Auth requerida | Status éxito |
|---|---|---|---|---|---|
| HU01 | Login | `/api/auth/login` | POST | No | 200 |
| HU03 | Logout | `/api/auth/logout` | POST | JWT (propio) | 200 |
| HU04 | Registrar Cliente | `/api/usuario/registrar` | POST | No | 201 |
| HU05 | Registrar Proveedor | `/api/usuario/registrar/proveedor` | POST | JWT + rol `administrador` | 201 |

---

## Notas Técnicas

### Autenticación

- Los JWT son propios de la aplicación (`JwtUtil`, firmados con `app.jwt.secret`), **no** son los JWT que emite Supabase Auth directamente — se generan después de validar la contraseña contra Supabase.
- Sesión stateless: cada request se autentica independientemente vía el header `Authorization: Bearer <token>`, sin sesión HTTP (`SessionCreationPolicy.STATELESS`).
- `JwtFilter` corre antes de la autorización en cada request: valida el token, revisa la blacklist de logout, revisa inactividad, y carga el rol del usuario como `ROLE_<rol>` en el contexto de seguridad.
- Un token en la blacklist de logout sigue siendo válido criptográficamente pero es rechazado igual — la blacklist se revisa antes de aceptar el token.

### Validación

- Todos los DTOs de request usan Bean Validation (`@NotBlank`, `@Email`) evaluado automáticamente por `@Valid` en el controller.
- Los errores de validación los captura `GlobalExceptionHandler` (`MethodArgumentNotValidException` → 400) con el detalle de qué campo falló.

### Roles

- `Rol` es un enum (`cliente`, `proveedor`, y presumiblemente `administrador`) mapeado en el entity `Usuario` con `@Enumerated(EnumType.STRING)`.
- El registro público (HU04) nunca deja que el cliente elija su propio rol — el controller lo fuerza explícitamente, evitando escalación de privilegios vía el body del request.

---

## Testing con Postman

### Setup

1. Crea una **Environment** en Postman con las variables:
   - `base_url` → `https://fabrica-2026-2-eap05-1.onrender.com` (o `http://localhost:8080` en local)
   - `admin_token` → (se llena manualmente tras loguear un usuario con rol `administrador`)

### Orden recomendado de pruebas

1. **HU04 — Registrar Cliente**
   `POST {{base_url}}/api/usuario/registrar` con un email de prueba nuevo.
   Verificar 201 y guardar el `id` retornado.

2. **HU01 — Login (caso sin password)**
   `POST {{base_url}}/api/auth/login` con ese mismo email.
   Esperado: **401**, porque el usuario recién registrado no tiene contraseña todavía (requiere completar el flujo de reset password, fuera del alcance de este documento).

3. **HU01 — Login (usuario con password ya configurado)**
   Usar un usuario que ya tenga contraseña establecida.
   Verificar 200 y copiar el `token` de la respuesta a una variable de Postman (por ejemplo con un script de "Tests": `pm.environment.set("token", pm.response.json().token)`).

4. **HU05 — Registrar Proveedor (sin rol admin)**
   `POST {{base_url}}/api/usuario/registrar/proveedor` con `Authorization: Bearer {{token}}` de un usuario `cliente`.
   Esperado: **403**.

5. **HU05 — Registrar Proveedor (con rol admin)**
   Mismo request pero con `Authorization: Bearer {{admin_token}}`.
   Esperado: **201**.

6. **HU03 — Logout**
   `POST {{base_url}}/api/auth/logout` con `Authorization: Bearer {{token}}`.
   Verificar 200, y luego repetir cualquier request protegido con ese mismo token — debe fallar (el token quedó en la blacklist).

7. **HU04 — Email duplicado**
   Repetir el registro del paso 1 con el mismo email.
   Esperado: **409**.
