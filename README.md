# API de Gestión de Pólizas

API Spring Boot (Módulo 2) para gestión básica de pólizas individuales y colectivas.

## Requisitos

- Java 21+
- IntelliJ IDEA (recomendado) o Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Cómo ejecutar

### IntelliJ IDEA (recomendado)

1. Abre el proyecto: **File → Open** y selecciona la carpeta `policy-management`.
2. Espera a que IntelliJ importe Maven
3. Abre la clase `PolicyManagementApplication`.
4. Haz clic en el ícono verde ▶ junto a `main`, o usa **Run → Run 'PolicyManagementApplication'**.
5. Revisa la consola de Run: cuando aparezca `Started PolicyManagementApplication`, la API está lista.

La aplicación queda en `http://localhost:8080`.

Los logs del mock CORE también se ven en esa misma consola de Run.

Usa H2 en memoria. Al arrancar se cargan 2 pólizas de ejemplo (1 individual y 1 colectiva).

### Alternativa: terminal (Maven)

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

## Seguridad

Todas las peticiones requieren el header:

```
x-api-key: 123456
```

Sin ese header la API responde `401 Unauthorized`.

## Endpoints

### Pólizas

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/polizas?tipo=&estado=` | Listar por tipo y/o estado |
| GET | `/api/v1/polizas/{id}/riesgos` | Listar riesgos de una póliza |
| POST | `/api/v1/polizas/{id}/renovar` | Renovar (+IPC) → estado `RENOVADA` |
| POST | `/api/v1/polizas/{id}/cancelar` | Cancelar póliza y todos sus riesgos |
| POST | `/api/v1/polizas/{id}/riesgos` | Agregar riesgo (solo `COLECTIVA`) |

### Riesgos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/riesgos/{id}/cancelar` | Cancelar un riesgo |

### Mock CORE

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/core-mock/evento` | Simula envío al CORE (solo log) |

Body del mock:

```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

Las operaciones que modifican pólizas/riesgos invocan este mock automáticamente.

## Ejemplos

### PowerShell (Windows)

Listar pólizas activas colectivas:

```powershell
Invoke-RestMethod -Headers @{ "x-api-key" = "123456" } `
  -Uri "http://localhost:8080/api/v1/polizas?tipo=COLECTIVA&estado=ACTIVA"
```

Renovar:

```powershell
Invoke-RestMethod -Method POST -Headers @{ "x-api-key" = "123456" } `
  -Uri "http://localhost:8080/api/v1/polizas/1/renovar"
```

Agregar riesgo (póliza colectiva, id=2 en el seed):

```powershell
$body = @{
  direccionInmueble = "Calle 10 #5-20"
  asegurado = "Juan"
  beneficiario = "Maria"
} | ConvertTo-Json

Invoke-RestMethod -Method POST `
  -Headers @{ "x-api-key" = "123456" } `
  -ContentType "application/json" `
  -Uri "http://localhost:8080/api/v1/polizas/2/riesgos" `
  -Body $body
```

Invocar mock CORE:

```powershell
$body = @{ evento = "ACTUALIZACION"; polizaId = 555 } | ConvertTo-Json

Invoke-RestMethod -Method POST `
  -Headers @{ "x-api-key" = "123456" } `
  -ContentType "application/json" `
  -Uri "http://localhost:8080/core-mock/evento" `
  -Body $body
```

> Nota: en PowerShell `curl` es alias de `Invoke-WebRequest`. Usa `Invoke-RestMethod` o `curl.exe`.

### curl (Git Bash / Linux / macOS)

```bash
curl -H "x-api-key: 123456" "http://localhost:8080/api/v1/polizas?tipo=COLECTIVA&estado=ACTIVA"

curl -X POST -H "x-api-key: 123456" http://localhost:8080/api/v1/polizas/1/renovar

curl -X POST http://localhost:8080/api/v1/polizas/2/riesgos \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"direccionInmueble":"Calle 10 #5-20","asegurado":"Juan","beneficiario":"Maria"}'

curl -X POST http://localhost:8080/core-mock/evento \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}'
```

## Reglas de negocio

- Una póliza **individual** solo puede tener **1 riesgo** (no se permiten altas adicionales).
- No se puede **renovar** una póliza **cancelada**.
- Cancelar una póliza cancela **todos** sus riesgos.
- Agregar riesgo valida que el tipo sea **COLECTIVA**.
- Renovación: incrementa canon y prima según IPC (`app.ipc-rate`, default 5%) y deja estado `RENOVADA`.

## Estructura

```
controller/   endpoints REST
service/      reglas de negocio
repository/   acceso a datos (JPA)
model/        Poliza (Policy) y Riesgo (Risk)
dto/          request/response
client/       llamada al mock CORE
security/     filtro x-api-key
exception/    manejo global de errores
config/       seed de datos
```

## Configuración relevante

| Propiedad | Valor | Descripción |
|-----------|-------|-------------|
| `app.api-key` | `123456` | API key obligatoria |
| `app.ipc-rate` | `0.05` | Incremento IPC en renovación |
| `app.core-mock-url` | `http://localhost:8080/core-mock/evento` | URL del mock CORE |
