# Historias de Usuario Implementadas - Sprint-1

## HU-01: Registrar Nuevo Usuario
**Estado:** ✅ COMPLETADA

Endpoint: POST /api/usuario/registrar
- Usuario ingresa email, nombre, teléfono, dirección
- Sistema crea en auth.users + public.usuario
- Retorna 201 con UUID

## HU-02: Iniciar Sesión (Login)
**Estado:** ⚠️ IMPLEMENTADA (limitación)

Endpoint: POST /api/auth/login
- Usuario ingresa email y contraseña
- Sistema valida contra Supabase Auth
- Retorna JWT si es correcto
- **LIMITACIÓN:** Usuarios sin password (Mailtrap offline)

## HU-03: Cerrar Sesión (Logout)
**Estado:** ✅ COMPLETADA

Endpoint: POST /api/auth/logout
- Usuario autenticado cierra sesión
- Revoca JWT en Supabase Auth
- Retorna 200 OK

## HU-04: Resetear Contraseña
**Estado:** ⚠️ IMPLEMENTADA (limitación)

Endpoint: POST /api/auth/reset-password
- Usuario recibe email con token
- Ingresa nueva contraseña
- Sistema valida token y actualiza en Supabase
- **LIMITACIÓN:** Mailtrap falla (credenciales offline)
