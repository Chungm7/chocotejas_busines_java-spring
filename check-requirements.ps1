# Script de verificación pre-ejecución
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verificación Pre-Ejecución" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$allOk = $true

# 1. Verificar Docker
Write-Host "[1/5] Verificando Docker Desktop..." -ForegroundColor Yellow
$dockerVersion = docker --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✅ Docker instalado: $dockerVersion" -ForegroundColor Green

    $dockerRunning = docker info 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ Docker está corriendo" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Docker no está corriendo. Inicia Docker Desktop." -ForegroundColor Red
        $allOk = $false
    }
} else {
    Write-Host "  ❌ Docker no está instalado" -ForegroundColor Red
    $allOk = $false
}
Write-Host ""

# 2. Verificar Docker Compose
Write-Host "[2/5] Verificando Docker Compose..." -ForegroundColor Yellow
$composeVersion = docker-compose --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✅ Docker Compose instalado: $composeVersion" -ForegroundColor Green
} else {
    Write-Host "  ❌ Docker Compose no está instalado" -ForegroundColor Red
    $allOk = $false
}
Write-Host ""

# 3. Verificar archivo .env
Write-Host "[3/5] Verificando archivo .env..." -ForegroundColor Yellow
if (Test-Path ".env") {
    Write-Host "  ✅ Archivo .env existe" -ForegroundColor Green

    # Verificar variables importantes
    $envContent = Get-Content ".env" -Raw
    $requiredVars = @(
        "MYSQL_PORT",
        "MYSQL_ROOT_PASSWORD",
        "SISTEMA_CHOCOTEJAS_PORT",
        "SPRING_DATASOURCE_URL"
    )

    $missingVars = @()
    foreach ($var in $requiredVars) {
        if ($envContent -notmatch $var) {
            $missingVars += $var
        }
    }

    if ($missingVars.Count -eq 0) {
        Write-Host "  ✅ Variables requeridas presentes" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Variables faltantes: $($missingVars -join ', ')" -ForegroundColor Yellow
    }
} else {
    Write-Host "  ⚠️  Archivo .env no existe" -ForegroundColor Yellow
    Write-Host "  📝 Creando desde .env.example..." -ForegroundColor Gray

    if (Test-Path ".env.example") {
        Copy-Item ".env.example" ".env"
        Write-Host "  ✅ Archivo .env creado" -ForegroundColor Green
    } else {
        Write-Host "  ❌ .env.example tampoco existe" -ForegroundColor Red
        $allOk = $false
    }
}
Write-Host ""

# 4. Verificar estructura de directorios Node
Write-Host "[4/5] Verificando servicios Node..." -ForegroundColor Yellow
$nodeServices = @("service-1", "service-2", "service-3")
$nodeOk = $true

foreach ($service in $nodeServices) {
    $path = "node-services\$service"
    if (Test-Path $path) {
        Write-Host "  ✅ $service existe" -ForegroundColor Green

        # Verificar archivos necesarios
        $requiredFiles = @("index.js", "package.json", "Dockerfile")
        foreach ($file in $requiredFiles) {
            if (-not (Test-Path "$path\$file")) {
                Write-Host "    ❌ Falta $file en $service" -ForegroundColor Red
                $nodeOk = $false
            }
        }
    } else {
        Write-Host "  ❌ $service no existe" -ForegroundColor Red
        $nodeOk = $false
    }
}

if ($nodeOk) {
    Write-Host "  ✅ Todos los servicios Node están completos" -ForegroundColor Green
}
Write-Host ""

# 5. Verificar puertos disponibles
Write-Host "[5/5] Verificando puertos..." -ForegroundColor Yellow
$ports = @(8080, 3001, 3002, 3003, 3306, 8081)
$portsOk = $true

foreach ($port in $ports) {
    $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue -ErrorAction SilentlyContinue -InformationLevel Quiet
    if ($connection) {
        Write-Host "  ⚠️  Puerto $port ya está en uso" -ForegroundColor Yellow
        $portsOk = $false
    } else {
        Write-Host "  ✅ Puerto $port disponible" -ForegroundColor Green
    }
}

if (-not $portsOk) {
    Write-Host "  💡 Si los puertos están en uso por contenedores antiguos, ejecuta: docker-compose down" -ForegroundColor Gray
}
Write-Host ""

# Resumen
Write-Host "========================================" -ForegroundColor Cyan
if ($allOk -and $nodeOk) {
    Write-Host "✅ ¡Todo listo para ejecutar!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Puedes continuar con:" -ForegroundColor White
    Write-Host "  .\start.ps1" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  Hay algunos problemas que resolver" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Por favor, corrige los errores antes de continuar" -ForegroundColor Gray
}
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

