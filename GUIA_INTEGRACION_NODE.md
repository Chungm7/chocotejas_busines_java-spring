# Guía Completa: Integración de Servicios Node.js con Spring Boot + Thymeleaf

## 📋 Índice
1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Servicios de Node.js](#servicios-de-nodejs)
4. [Pasos para Ejecución Limpia](#pasos-para-ejecución-limpia)
5. [Integración con el Frontend](#integración-con-el-frontend)
6. [Solución de Problemas](#solución-de-problemas)

---

## 📝 Descripción General

Este proyecto integra **3 servicios de Node.js** con un sistema Spring Boot que usa Thymeleaf para el frontend. Los servicios proporcionan datos en tiempo real sobre:

1. **Service 1 (Puerto 3001)**: Estadísticas de Ventas
2. **Service 2 (Puerto 3002)**: Análisis de Productos  
3. **Service 3 (Puerto 3003)**: Reportes de Clientes

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose Network                    │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐    │
│  │ Node Service │   │ Node Service │   │ Node Service │    │
│  │      1       │   │      2       │   │      3       │    │
│  │  Puerto 3001 │   │  Puerto 3002 │   │  Puerto 3003 │    │
│  └──────┬───────┘   └──────┬───────┘   └──────┬───────┘    │
│         │                  │                  │              │
│         └──────────────────┼──────────────────┘              │
│                            │                                 │
│                   ┌────────▼────────┐                        │
│                   │  Spring Boot    │                        │
│                   │  Application    │                        │
│                   │  Puerto 8083    │                        │
│                   └────────┬────────┘                        │
│                            │                                 │
│                   ┌────────▼────────┐                        │
│                   │  MySQL Database │                        │
│                   │  Puerto 3306    │                        │
│                   └─────────────────┘                        │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Servicios de Node.js

### Service 1: Estadísticas de Ventas

**Puerto**: 3001  
**Endpoints**:
- `GET /` - Información del servicio
- `GET /health` - Estado de salud
- `GET /api/data` - Datos de estadísticas

**Respuesta de ejemplo**:
```json
{
  "serviceName": "Estadísticas de Ventas",
  "status": "success",
  "message": "Datos de estadísticas obtenidos correctamente",
  "data": {
    "totalVentas": 15420.50,
    "ventasHoy": 8,
    "ventasMes": 142,
    "promedioVenta": 108.60,
    "tendencia": "+12.5%",
    "ultimaActualizacion": "2025-12-02T20:00:00.000Z"
  }
}
```

### Service 2: Análisis de Productos

**Puerto**: 3002  
**Endpoints**:
- `GET /` - Información del servicio
- `GET /health` - Estado de salud
- `GET /api/data` - Análisis de productos

**Respuesta de ejemplo**:
```json
{
  "serviceName": "Análisis de Productos",
  "status": "success",
  "message": "Análisis de productos obtenido correctamente",
  "data": {
    "totalProductos": 47,
    "productosActivos": 42,
    "productosBajoStock": 5,
    "categorias": 8,
    "masVendido": "Chocoteja Clásica",
    "stockTotal": 1250,
    "ultimaActualizacion": "2025-12-02T20:00:00.000Z"
  }
}
```

### Service 3: Reportes de Clientes

**Puerto**: 3003  
**Endpoints**:
- `GET /` - Información del servicio
- `GET /health` - Estado de salud
- `GET /api/data` - Reportes de clientes

**Respuesta de ejemplo**:
```json
{
  "serviceName": "Reportes de Clientes",
  "status": "success",
  "message": "Reportes de clientes obtenidos correctamente",
  "data": {
    "totalClientes": 328,
    "clientesActivos": 289,
    "nuevosEsteMes": 24,
    "clientesFrecuentes": 87,
    "tasaRetencion": "88.2%",
    "promedioCompras": 4.7,
    "ultimaActualizacion": "2025-12-02T20:00:00.000Z"
  }
}
```

---

## 📦 Pasos para Ejecución Limpia

### Paso 1: Verificar Prerequisitos

Asegúrate de tener instalado:
- Docker Desktop (versión 20.10 o superior)
- Docker Compose (versión 1.29 o superior)

```powershell
# Verificar versiones
docker --version
docker-compose --version
```

### Paso 2: Limpiar el Sistema (Opcional pero Recomendado)

Si tuviste problemas anteriores, limpia todo:

```powershell
# Detener todos los contenedores
docker-compose down

# Limpiar imágenes y cachés (CUIDADO: elimina todo)
docker system prune -af

# O solo limpiar este proyecto
docker-compose down --rmi all --volumes
```

### Paso 3: Verificar Archivos de los Servicios Node

Asegúrate de que los archivos `index.js` de cada servicio estén correctos:

**`node-services/service-1/index.js`**:
```javascript
const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3001;
const SERVICE_NAME = process.env.SERVICE_NAME || 'Estadísticas de Ventas';

// Middleware
app.use(cors());
app.use(express.json());

// Endpoint principal de datos
app.get('/api/data', (req, res) => {
    res.json({
        serviceName: SERVICE_NAME,
        status: 'success',
        message: 'Datos de estadísticas obtenidos correctamente',
        data: {
            totalVentas: 15420.50,
            ventasHoy: 8,
            ventasMes: 142,
            promedioVenta: 108.60,
            tendencia: '+12.5%',
            ultimaActualizacion: new Date().toISOString()
        }
    });
});

// Endpoint de salud
app.get('/health', (req, res) => {
    res.json({
        status: 'healthy',
        service: SERVICE_NAME,
        timestamp: new Date().toISOString()
    });
});

// Endpoint raíz
app.get('/', (req, res) => {
    res.json({
        message: `${SERVICE_NAME} está funcionando`,
        endpoints: {
            data: '/api/data',
            health: '/health'
        }
    });
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`${SERVICE_NAME} corriendo en puerto ${PORT}`);
});
```

*(Los archivos service-2 y service-3 son similares, solo cambian los datos)*

### Paso 4: Construir y Levantar los Servicios

```powershell
# Desde la raíz del proyecto
docker-compose up --build -d
```

**Explicación**:
- `up`: Levanta los servicios
- `--build`: Reconstruye las imágenes
- `-d`: Modo detached (segundo plano)

### Paso 5: Verificar que los Servicios Estén Corriendo

```powershell
# Ver todos los contenedores
docker-compose ps

# Ver logs de los servicios Node
docker-compose logs node-service-1 node-service-2 node-service-3

# Ver logs en tiempo real
docker-compose logs -f node-service-1
```

**Deberías ver**:
```
node-service-1 | Estadísticas de Ventas corriendo en puerto 3001
node-service-2 | Análisis de Productos corriendo en puerto 3002
node-service-3 | Reportes de Clientes corriendo en puerto 3003
```

### Paso 6: Probar los Servicios

```powershell
# Probar Service 1
curl http://localhost:3001/api/data

# Probar Service 2
curl http://localhost:3002/api/data

# Probar Service 3
curl http://localhost:3003/api/data

# Verificar salud de todos
curl http://localhost:3001/health
curl http://localhost:3002/health
curl http://localhost:3003/health
```

---

## 🌐 Integración con el Frontend

### Archivo JavaScript de Integración

Ya se creó el archivo en:  
`src/main/resources/static/js/gestion/node-services-integration.js`

Este archivo proporciona funciones para consumir los servicios:

```javascript
// Cargar datos de estadísticas de ventas
async function obtenerEstadisticasVentas() {
    const response = await fetch('http://localhost:3001/api/data');
    return await response.json();
}

// Cargar datos de análisis de productos
async function obtenerAnalisisProductos() {
    const response = await fetch('http://localhost:3002/api/data');
    return await response.json();
}

// Cargar datos de reportes de clientes
async function obtenerReportesClientes() {
    const response = await fetch('http://localhost:3003/api/data');
    return await response.json();
}

// Cargar todos los datos al mismo tiempo
async function cargarDatosServicios() {
    const [ventas, productos, clientes] = await Promise.all([
        obtenerEstadisticasVentas(),
        obtenerAnalisisProductos(),
        obtenerReportesClientes()
    ]);
    
    // Renderizar en el DOM
    renderizarEstadisticasVentas(ventas);
    renderizarAnalisisProductos(productos);
    renderizarReportesClientes(clientes);
}
```

### Crear una Página HTML para Visualizar los Datos

Crea o modifica el archivo:  
`src/main/resources/templates/gestion/gestion-dashboard.html`

```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Integración Node Services</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar (si tienes) -->
            <div th:replace="~{gestion/gestion-sidebar :: sidebar}"></div>
            
            <!-- Contenido Principal -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">
                        <i class="fas fa-chart-line"></i> Dashboard de Análisis
                    </h1>
                    <button class="btn btn-sm btn-primary" onclick="NodeServices.cargarDatosServicios()">
                        <i class="fas fa-sync-alt"></i> Actualizar Datos
                    </button>
                </div>

                <!-- Indicador de carga -->
                <div id="loading-servicios" class="text-center my-5" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                    <p class="mt-3">Cargando datos de los servicios...</p>
                </div>

                <!-- Mensajes de error -->
                <div id="error-servicios"></div>

                <!-- Contenedor para Estadísticas de Ventas -->
                <div id="estadisticas-ventas" class="mb-4"></div>

                <!-- Contenedor para Análisis de Productos -->
                <div id="analisis-productos" class="mb-4"></div>

                <!-- Contenedor para Reportes de Clientes -->
                <div id="reportes-clientes" class="mb-4"></div>
            </main>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Script de integración con Node Services -->
    <script th:src="@{/js/gestion/node-services-integration.js}"></script>
    
    <!-- Inicializar -->
    <script>
        // Cargar datos cuando la página está lista
        document.addEventListener('DOMContentLoaded', function() {
            // Cargar datos inmediatamente
            NodeServices.cargarDatosServicios();
            
            // Actualizar automáticamente cada 30 segundos
            NodeServices.iniciarActualizacionAutomatica(30000);
        });
    </script>
</body>
</html>
```

### Crear un Controlador Spring Boot

Crea o modifica el controlador para servir la página:

```java
package com.example.sistema_venta_chocotejas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gestion")
public class DashboardController {

    @GetMapping("/dashboard")
    public String mostrarDashboard() {
        return "gestion/gestion-dashboard";
    }
    
    @GetMapping("/integracion-node")
    public String mostrarIntegracionNode() {
        return "gestion/gestion-integracion-node";
    }
}
```

---

## 🔧 Solución de Problemas

### Problema 1: Los servicios Node no se levantan

**Síntoma**: Errores de sintaxis o contenedores que se cierran inmediatamente.

**Solución**:
```powershell
# 1. Detener todo
docker-compose down

# 2. Verificar archivos index.js
# Asegúrate de que no tengan código duplicado o invertido

# 3. Reconstruir desde cero
docker-compose build --no-cache node-service-1 node-service-2 node-service-3

# 4. Levantar solo los servicios Node para ver logs
docker-compose up node-service-1 node-service-2 node-service-3
```

### Problema 2: Error CORS al consumir desde el frontend

**Síntoma**: Error "Access to fetch blocked by CORS policy"

**Solución**: Los servicios ya tienen CORS habilitado. Si persiste:

1. Verifica que los servicios usen `app.use(cors())`
2. O configura CORS específico:

```javascript
app.use(cors({
    origin: 'http://localhost:8083',
    credentials: true
}));
```

### Problema 3: No se pueden alcanzar los servicios

**Síntoma**: `ERR_CONNECTION_REFUSED`

**Solución**:
```powershell
# 1. Verificar que los puertos estén expuestos
docker-compose ps

# 2. Verificar que no haya otros servicios usando los puertos
netstat -ano | findstr :3001
netstat -ano | findstr :3002
netstat -ano | findstr :3003

# 3. Reiniciar servicios
docker-compose restart node-service-1 node-service-2 node-service-3
```

### Problema 4: Los datos no se actualizan

**Síntoma**: Los datos en el frontend no cambian.

**Solución**:
```javascript
// Verificar que la función de actualización automática esté activa
// En la consola del navegador:
console.log('Actualizando datos...');
NodeServices.cargarDatosServicios();
```

---

## 📊 Testing de los Servicios

### Usando curl (PowerShell)

```powershell
# Test completo de todos los servicios
$services = @(3001, 3002, 3003)
foreach ($port in $services) {
    Write-Host "Testing service on port $port"
    curl "http://localhost:$port/api/data" | ConvertFrom-Json | ConvertTo-Json -Depth 10
    Write-Host "`n---`n"
}
```

### Usando el navegador

Simplemente abre:
- http://localhost:3001/api/data
- http://localhost:3002/api/data
- http://localhost:3003/api/data

---

## 📈 Comandos Útiles

```powershell
# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f node-service-1

# Reiniciar un servicio
docker-compose restart node-service-1

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Ver estadísticas de recursos
docker stats

# Entrar a un contenedor
docker-compose exec node-service-1 sh

# Ver IP de los contenedores
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' node-service-1
```

---

## 🎯 Próximos Pasos

1. **Personalizar los datos**: Modifica los servicios Node para conectar con bases de datos reales
2. **Agregar autenticación**: Protege los endpoints con JWT o API keys
3. **Implementar WebSockets**: Para datos en tiempo real sin polling
4. **Agregar gráficos**: Usa Chart.js o D3.js para visualizar los datos
5. **Crear pruebas**: Agregar tests unitarios y de integración

---

## 📝 Notas Finales

- Los servicios Node son **independientes** del sistema Spring Boot
- Pueden escalarse horizontalmente agregando más instancias
- Los datos son **estáticos** por ahora, pero pueden conectarse a bases de datos
- La comunicación es mediante **HTTP REST APIs**
- El frontend usa **Fetch API** para consumir los servicios

---

**¡Éxito con tu proyecto! 🚀**

