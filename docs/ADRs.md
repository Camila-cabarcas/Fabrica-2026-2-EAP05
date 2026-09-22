# Architecture Decision Records (ADRs) - Sprint-1

## ADR-001: Supabase Auth + Admin API para Gestión de Usuarios

**Status:** ✅ ACCEPTED & IMPLEMENTED

### Context
Se necesitaba crear un sistema de autenticación seguro para usuarios registrados en un servidor Spring Boot deployado en Render, conectado a Supabase PostgreSQL.

### Decision
Usar **Supabase Auth (OAuth managed) + Admin API** para crear usuarios en uth.users y luego en public.usuario.

### Implementation
- Clase: SupabaseAuthService.java
- Método: crearUsuarioAuth(email)
- Flujo: POST /auth/v1/admin/users → INSERT public.usuario

### Consequences
✅ Seguridad delegada a Supabase
❌ Dependencia de Supabase Auth API

---

## ADR-002: HikariCP Pool Limitado (max=5)

**Status:** ✅ ACCEPTED & IMPLEMENTED

### Context
Supabase permite máximo 15 conexiones en session pooler. Spring Boot intentaba crear 20+.

### Decision
Limitar HikariCP a 5 conexiones:
- maximum-pool-size=5
- minimum-idle=1

### Implementation
- Archivo: application.properties (líneas 37-39)

### Consequences
✅ Deployment estable
❌ Posible cuello de botella en picos

---

## ADR-003: ddl-auto=none + Schema Manual

**Status:** ✅ ACCEPTED & IMPLEMENTED

### Context
ddl-auto=update/validate causaban timeouts y EMAXCONNSESSION en startup.

### Decision
Establecer ddl-auto=none. Cambios de schema se aplican manualmente en Supabase.

### Implementation
- application.properties (línea 8)
- render.yaml (línea 15)

### Consequences
✅ Startup rápido
❌ RIESGO: Si olvidan ALTER TABLE, falla en producción
