# Script de Prueba de Servicios Node.js
# Descripción: Prueba la conectividad y respuestas de los servicios

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Prueba de Servicios Node.js           " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

function Test-NodeService {
    param(
        [int]$Port,
        [string]$ServiceName
    )

    Write-Host "Probando: $ServiceName (Puerto $Port)" -ForegroundColor Yellow
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

    # Probar endpoint raíz
    try {
        Write-Host "  GET http://localhost:$Port/" -ForegroundColor Cyan
        $response = Invoke-RestMethod -Uri "http://localhost:$Port/" -Method Get -TimeoutSec 5
        Write-Host "  ✓ Respuesta recibida" -ForegroundColor Green
        Write-Host "  Mensaje: $($response.message)" -ForegroundColor White
        Write-Host ""
    } catch {
        Write-Host "  ✗ Error al conectar: $_" -ForegroundColor Red
        Write-Host ""
        return $false
    }

    # Probar endpoint de salud
    try {
        Write-Host "  GET http://localhost:$Port/health" -ForegroundColor Cyan
        $response = Invoke-RestMethod -Uri "http://localhost:$Port/health" -Method Get -TimeoutSec 5
        Write-Host "  ✓ Estado: $($response.status)" -ForegroundColor Green
        Write-Host "  Servicio: $($response.service)" -ForegroundColor White
        Write-Host ""
    } catch {
        Write-Host "  ✗ Error al verificar salud: $_" -ForegroundColor Red
        Write-Host ""
        return $false
    }

    # Probar endpoint de datos
    try {
        Write-Host "  GET http://localhost:$Port/api/data" -ForegroundColor Cyan
        $response = Invoke-RestMethod -Uri "http://localhost:$Port/api/data" -Method Get -TimeoutSec 5
        Write-Host "  ✓ Datos recibidos correctamente" -ForegroundColor Green
        Write-Host "  Status: $($response.status)" -ForegroundColor White
        Write-Host "  Mensaje: $($response.message)" -ForegroundColor White

        # Mostrar datos en formato JSON bonito
        Write-Host ""
        Write-Host "  Datos:" -ForegroundColor Cyan
        $response.data | ConvertTo-Json -Depth 5 | ForEach-Object {
            Write-Host "  $_" -ForegroundColor Gray
        }
        Write-Host ""
    } catch {
        Write-Host "  ✗ Error al obtener datos: $_" -ForegroundColor Red
        Write-Host ""
        return $false
    }

    return $true
}

# Verificar que Docker Compose esté corriendo
Write-Host "Verificando que los servicios estén corriendo..." -ForegroundColor Yellow
try {
    $containers = docker-compose ps --services 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Los servicios no están corriendo" -ForegroundColor Red
        Write-Host "Por favor, ejecuta: docker-compose up -d" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Error al verificar servicios: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Servicios detectados. Iniciando pruebas..." -ForegroundColor Green
Write-Host ""
Start-Sleep -Seconds 2

# Definir servicios a probar
$servicios = @(
    @{Port=3001; Nombre="Servicio 1 - Estadísticas de Ventas"},
    @{Port=3002; Nombre="Servicio 2 - Análisis de Productos"},
    @{Port=3003; Nombre="Servicio 3 - Reportes de Clientes"}
)

$resultados = @()

# Probar cada servicio
foreach ($servicio in $servicios) {
    $exito = Test-NodeService -Port $servicio.Port -Nombre $servicio.Nombre
    $resultados += @{
        Nombre = $servicio.Nombre
        Puerto = $servicio.Port
        Exito = $exito
    }
    Write-Host ""
}

# Resumen de resultados
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Resumen de Pruebas                    " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$exitosos = 0
$fallidos = 0

foreach ($resultado in $resultados) {
    $icono = if ($resultado.Exito) { "✓" } else { "✗" }
    $color = if ($resultado.Exito) { "Green" } else { "Red" }
    $estado = if ($resultado.Exito) { "EXITOSO" } else { "FALLIDO" }

    Write-Host "$icono $($resultado.Nombre) (Puerto $($resultado.Puerto)) - $estado" -ForegroundColor $color

    if ($resultado.Exito) {
        $exitosos++
    } else {
        $fallidos++
    }
}

Write-Host ""
Write-Host "Total: $exitosos exitosos, $fallidos fallidos" -ForegroundColor $(if ($fallidos -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

# Probar también el servicio Spring Boot (si está disponible)
Write-Host "Probando servicio Spring Boot..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8083" -Method Get -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✓ Aplicación Spring Boot está corriendo en http://localhost:8083" -ForegroundColor Green
    }
} catch {
    Write-Host "✗ Aplicación Spring Boot no responde o aún está iniciando" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Pruebas Completadas                   " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

if ($fallidos -eq 0) {
    Write-Host "¡Todos los servicios están funcionando correctamente! 🎉" -ForegroundColor Green
} else {
    Write-Host "Algunos servicios tienen problemas. Revisa los logs con:" -ForegroundColor Yellow
    Write-Host "  docker-compose logs -f" -ForegroundColor White
}

Write-Host ""

