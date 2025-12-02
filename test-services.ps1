# Script para verificar el estado de todos los servicios
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verificando Servicios" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Función para verificar un servicio HTTP
function Test-Service {
    param(
        [string]$Name,
        [string]$Url
    )

    try {
        $response = Invoke-WebRequest -Uri $Url -Method Get -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $Name" -ForegroundColor Green -NoNewline
            Write-Host " - OK" -ForegroundColor Gray
            return $true
        }
    }
    catch {
        Write-Host "❌ $Name" -ForegroundColor Red -NoNewline
        Write-Host " - Error: $($_.Exception.Message)" -ForegroundColor Gray
        return $false
    }
}

# Verificar servicios Node
Write-Host "Verificando Servicios Node:" -ForegroundColor Yellow
Write-Host ""

$service1 = Test-Service -Name "Node Service 1 (Ventas)     " -Url "http://localhost:3001/health"
$service2 = Test-Service -Name "Node Service 2 (Productos)  " -Url "http://localhost:3002/health"
$service3 = Test-Service -Name "Node Service 3 (Clientes)   " -Url "http://localhost:3003/health"

Write-Host ""

# Verificar Spring Boot
Write-Host "Verificando Aplicación Spring Boot:" -ForegroundColor Yellow
Write-Host ""

$springBoot = Test-Service -Name "Spring Boot Application    " -Url "http://localhost:8080"

Write-Host ""

# Verificar PhpMyAdmin
Write-Host "Verificando Base de Datos:" -ForegroundColor Yellow
Write-Host ""

$phpmyadmin = Test-Service -Name "PhpMyAdmin                 " -Url "http://localhost:8081"

Write-Host ""

# Verificar integración
Write-Host "Verificando Integración:" -ForegroundColor Yellow
Write-Host ""

$integracion = Test-Service -Name "Vista Integración          " -Url "http://localhost:8080/gestion/integracion"
$apiAll = Test-Service -Name "API All Services           " -Url "http://localhost:8080/gestion/integracion/api/all"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

# Resumen
$total = 7
$exitosos = 0
if ($service1) { $exitosos++ }
if ($service2) { $exitosos++ }
if ($service3) { $exitosos++ }
if ($springBoot) { $exitosos++ }
if ($phpmyadmin) { $exitosos++ }
if ($integracion) { $exitosos++ }
if ($apiAll) { $exitosos++ }

Write-Host ""
Write-Host "Resumen: $exitosos/$total servicios funcionando" -ForegroundColor $(if ($exitosos -eq $total) { "Green" } else { "Yellow" })
Write-Host ""

if ($exitosos -eq $total) {
    Write-Host "🎉 ¡Todos los servicios están funcionando correctamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Accede a la integración en:" -ForegroundColor White
    Write-Host "http://localhost:8080/gestion/integracion" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  Algunos servicios no están respondiendo" -ForegroundColor Yellow
    Write-Host "Verifica los logs con: docker-compose logs -f" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

