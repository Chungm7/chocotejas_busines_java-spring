# Script para detener todos los servicios
Write-Host "Deteniendo servicios..." -ForegroundColor Yellow
docker-compose down
Write-Host "✅ Servicios detenidos" -ForegroundColor Green

