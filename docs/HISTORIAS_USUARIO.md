# Historias de Usuario Implementadas - Sprint-1

## HU-01: Registrar Nuevo Usuario
**Estado:**  COMPLETADA

Endpoint: POST /api/usuario/registrar
- Usuario ingresa email, nombre, teléfono, dirección
- Sistema crea en auth.users + public.usuario
- Retorna 201 con UUID

## HU-02: Iniciar Sesión (Login)
**Estado:** ⏳ PARCIALMENTE COMPLETA (Sprint-1)

Endpoint: POST /api/auth/login
- Usuario ingresa email y contraseña
- Sistema valida contra Supabase Auth
- Retorna JWT si es correcto
- **LIMITACIÓN:** Usuarios sin password (Mailtrap offline)
- ⏳ **[PENDIENTE SPRINT-2]** Testing end-to-end: verificar que JWT accede a rutas protegidas con usuarios que tengan password

### Por Qué No Se Completó en Sprint-1
- **Bloqueador:** Usuarios sin password (Mailtrap offline impide reset-password)
- **Gestión de tiempo:** Prioridad fue resolver EMAXCONNSESSION y deployment
- **Completitud:** Endpoint 90% (implementado), falta 10% (testing real con usuarios válidos)
- **Planificado:** Sprint-2 cuando Mailtrap esté configurado

## HU-03: Cerrar Sesión (Logout)
**Estado:**  COMPLETADA

Endpoint: POST /api/auth/logout
- Usuario autenticado cierra sesión
- Revoca JWT en Supabase Auth
- Retorna 200 OK

## HU-04: Resetear Contraseña
**Estado:**  IMPLEMENTADA (limitación)

Endpoint: POST /api/auth/reset-password
- Usuario recibe email con token
- Ingresa nueva contraseña
- Sistema valida token y actualiza en Supabase
- **LIMITACIÓN:** Mailtrap falla (credenciales offline)

## Resumen - Sprint-1

| HU | Feature | Estado | Completitud | Próximo Sprint |
|---|---|---|---|---|
| HU-01 | Registro |  Completa | 100% | - |
| HU-02 | Login |  Parcial | 90% | Testing E2E + password |
| HU-03 | Logout |  Completa | 100% | - |
| HU-04 | Reset Password |  Parcial | 80% | Configurar Mailtrap |
