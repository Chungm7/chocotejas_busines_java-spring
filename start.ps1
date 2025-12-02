# Script para iniciar la aplicación con Docker Compose
docker-compose logs -f
# Mostrar logs en tiempo real

Write-Host ""
Write-Host "Presiona Ctrl+C para detener el script (los servicios seguirán corriendo)" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Reiniciar:     docker-compose restart" -ForegroundColor Gray
Write-Host "   Detener:       docker-compose down" -ForegroundColor Gray
Write-Host "   Ver logs:      docker-compose logs -f" -ForegroundColor Gray
Write-Host "💡 Comandos útiles:" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "     PhpMyAdmin: http://localhost:8081" -ForegroundColor White
Write-Host "     MySQL:      localhost:3306" -ForegroundColor White
Write-Host "  🗄️  Base de Datos:" -ForegroundColor Yellow
Write-Host ""
Write-Host "     Service 3 (Clientes):  http://localhost:3003" -ForegroundColor White
Write-Host "     Service 2 (Productos): http://localhost:3002" -ForegroundColor White
Write-Host "     Service 1 (Ventas):    http://localhost:3001" -ForegroundColor White
Write-Host "  📦 Servicios Node:" -ForegroundColor Yellow
Write-Host ""
Write-Host "     http://localhost:8080/gestion/integracion" -ForegroundColor White
Write-Host "  🔗 Integración Servicios Node:" -ForegroundColor Yellow
Write-Host ""
Write-Host "     http://localhost:8080" -ForegroundColor White
Write-Host "  🌐 Aplicación Principal:" -ForegroundColor Yellow
Write-Host ""
Write-Host "📊 Servicios disponibles:" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  🚀 Sistema Iniciado Exitosamente" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
# Mostrar información de acceso

Write-Host ""
Write-Host "✅ Sistema listo" -ForegroundColor Green
Start-Sleep -Seconds 10
Write-Host "[4/4] Esperando a que los servicios estén listos..." -ForegroundColor Yellow
# Esperar a que los servicios estén listos

Write-Host ""
Write-Host "✅ Servicios levantados exitosamente" -ForegroundColor Green
}
    exit 1
    Write-Host "❌ Error al construir los servicios" -ForegroundColor Red
if ($LASTEXITCODE -ne 0) {

docker-compose up --build -d
Write-Host "Esto puede tomar varios minutos la primera vez..." -ForegroundColor Gray
Write-Host "[3/4] Construyendo y levantando servicios..." -ForegroundColor Yellow
# Construir y levantar servicios

Write-Host ""
Write-Host "✅ Limpieza completada" -ForegroundColor Green
docker-compose down 2>&1 | Out-Null
Write-Host "[2/4] Limpiando contenedores previos..." -ForegroundColor Yellow
# Detener contenedores previos si existen

Write-Host ""
Write-Host "✅ Docker está corriendo" -ForegroundColor Green
}
    exit 1
    Write-Host "❌ Error: Docker no está corriendo. Por favor inicia Docker Desktop." -ForegroundColor Red
if ($LASTEXITCODE -ne 0) {
$dockerRunning = docker info 2>&1
Write-Host "[1/4] Verificando Docker..." -ForegroundColor Yellow
# Verificar si Docker está corriendo

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Con Integración de Servicios Node" -ForegroundColor Cyan
Write-Host "  Iniciando Sistema Chocotejas" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Ejecutar desde la raíz del proyecto

