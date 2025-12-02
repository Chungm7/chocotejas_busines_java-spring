# 📘 GUÍA COMPLETA: Cómo Utilizar los Servicios Node.js en tu Aplicación

**Fecha**: 2 de Diciembre, 2025  
**Sistema**: Chocotejas Business  
**Autor**: Sistema de Gestión

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Verificación de Servicios](#verificación-de-servicios)
3. [Arquitectura y Endpoints](#arquitectura-y-endpoints)
4. [Integración en el Frontend](#integración-en-el-frontend)
5. [Ejemplos Prácticos](#ejemplos-prácticos)
6. [Casos de Uso Reales](#casos-de-uso-reales)
7. [Personalización Avanzada](#personalización-avanzada)
8. [Solución de Problemas](#solución-de-problemas)
9. [Mejores Prácticas](#mejores-prácticas)

---

## 🎯 Introducción

Los 3 servicios de Node.js proporcionan datos en tiempo real para tu aplicación Spring Boot. Cada servicio es independiente y puede ser consumido desde cualquier parte de tu frontend usando JavaScript.

### Servicios Disponibles

| Servicio | Puerto | Función | Endpoint Principal |
|----------|--------|---------|-------------------|
| **Service 1** | 3001 | Estadísticas de Ventas | `/api/data` |
| **Service 2** | 3002 | Análisis de Productos | `/api/data` |
| **Service 3** | 3003 | Reportes de Clientes | `/api/data` |

---

## ✅ Verificación de Servicios

### Paso 1: Verificar que los servicios estén corriendo

```powershell
# Ver el estado de todos los contenedores
docker-compose ps
```

**Resultado esperado:**
```
node-service-1    Up    0.0.0.0:3001->3001/tcp
node-service-2    Up    0.0.0.0:3002->3002/tcp
node-service-3    Up    0.0.0.0:3003->3003/tcp
```

### Paso 2: Probar los endpoints

```powershell
# Probar Service 1
curl http://localhost:3001/api/data

# Probar Service 2
curl http://localhost:3002/api/data

# Probar Service 3
curl http://localhost:3003/api/data
```

### Paso 3: Verificar salud de los servicios

```powershell
# Health check de cada servicio
curl http://localhost:3001/health
curl http://localhost:3002/health
curl http://localhost:3003/health
```

---

## 🏗️ Arquitectura y Endpoints

### Service 1: Estadísticas de Ventas (Puerto 3001)

#### Endpoints disponibles:

**1. Endpoint raíz** - Información del servicio
```http
GET http://localhost:3001/
```

**Respuesta:**
```json
{
  "message": "Estadísticas de Ventas está funcionando",
  "endpoints": {
    "data": "/api/data",
    "health": "/health"
  }
}
```

**2. Health Check** - Estado de salud
```http
GET http://localhost:3001/health
```

**Respuesta:**
```json
{
  "status": "healthy",
  "service": "Estadísticas de Ventas",
  "timestamp": "2025-12-02T22:00:00.000Z"
}
```

**3. Datos principales** - Estadísticas de ventas
```http
GET http://localhost:3001/api/data
```

**Respuesta:**
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
    "ultimaActualizacion": "2025-12-02T22:00:00.000Z"
  }
}
```

---

### Service 2: Análisis de Productos (Puerto 3002)

#### Endpoints disponibles:

**1. Endpoint raíz**
```http
GET http://localhost:3002/
```

**2. Health Check**
```http
GET http://localhost:3002/health
```

**3. Datos principales** - Análisis de productos
```http
GET http://localhost:3002/api/data
```

**Respuesta:**
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
    "ultimaActualizacion": "2025-12-02T22:00:00.000Z"
  }
}
```

---

### Service 3: Reportes de Clientes (Puerto 3003)

#### Endpoints disponibles:

**1. Endpoint raíz**
```http
GET http://localhost:3003/
```

**2. Health Check**
```http
GET http://localhost:3003/health
```

**3. Datos principales** - Reportes de clientes
```http
GET http://localhost:3003/api/data
```

**Respuesta:**
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
    "ultimaActualizacion": "2025-12-02T22:00:00.000Z"
  }
}
```

---

## 🌐 Integración en el Frontend

### Método 1: Usando el JavaScript de Integración (Recomendado)

Ya existe un archivo JavaScript que facilita el consumo de los servicios:  
`src/main/resources/static/js/gestion/node-services-integration.js`

#### Paso 1: Incluir el script en tu HTML

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Mi Página</title>
</head>
<body>
    <!-- Tu contenido aquí -->

    <!-- Incluir el script de integración -->
    <script th:src="@{/js/gestion/node-services-integration.js}"></script>
</body>
</html>
```

#### Paso 2: Crear contenedores para los datos

```html
<div class="container">
    <!-- Contenedor para estadísticas de ventas -->
    <div id="estadisticas-ventas"></div>
    
    <!-- Contenedor para análisis de productos -->
    <div id="analisis-productos"></div>
    
    <!-- Contenedor para reportes de clientes -->
    <div id="reportes-clientes"></div>
    
    <!-- Contenedor para mensajes de error -->
    <div id="error-servicios"></div>
    
    <!-- Indicador de carga -->
    <div id="loading-servicios" style="display: none;">
        Cargando datos...
    </div>
</div>
```

#### Paso 3: Cargar los datos

```html
<script>
    // Cargar todos los datos cuando la página esté lista
    document.addEventListener('DOMContentLoaded', function() {
        // Cargar datos de los 3 servicios
        NodeServices.cargarDatosServicios();
        
        // Opcional: Actualización automática cada 30 segundos
        NodeServices.iniciarActualizacionAutomatica(30000);
    });
</script>
```

---

### Método 2: Consumo Manual con Fetch API

Si prefieres tener más control, puedes consumir los servicios directamente:

#### Ejemplo 1: Obtener estadísticas de ventas

```javascript
async function obtenerEstadisticasVentas() {
    try {
        const response = await fetch('http://localhost:3001/api/data');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const datos = await response.json();
        console.log('Estadísticas:', datos);
        
        // Usar los datos
        const totalVentas = datos.data.totalVentas;
        const ventasHoy = datos.data.ventasHoy;
        
        // Actualizar el DOM
        document.getElementById('total-ventas').textContent = `S/. ${totalVentas}`;
        document.getElementById('ventas-hoy').textContent = ventasHoy;
        
    } catch (error) {
        console.error('Error al obtener estadísticas:', error);
    }
}

// Llamar la función
obtenerEstadisticasVentas();
```

#### Ejemplo 2: Obtener análisis de productos

```javascript
async function obtenerAnalisisProductos() {
    try {
        const response = await fetch('http://localhost:3002/api/data');
        const datos = await response.json();
        
        // Extraer datos específicos
        const totalProductos = datos.data.totalProductos;
        const productosBajoStock = datos.data.productosBajoStock;
        const masVendido = datos.data.masVendido;
        
        // Mostrar en el HTML
        document.getElementById('total-productos').textContent = totalProductos;
        document.getElementById('bajo-stock').textContent = productosBajoStock;
        document.getElementById('mas-vendido').textContent = masVendido;
        
    } catch (error) {
        console.error('Error:', error);
    }
}
```

#### Ejemplo 3: Obtener reportes de clientes

```javascript
async function obtenerReportesClientes() {
    try {
        const response = await fetch('http://localhost:3003/api/data');
        const datos = await response.json();
        
        // Usar los datos
        const totalClientes = datos.data.totalClientes;
        const tasaRetencion = datos.data.tasaRetencion;
        
        // Renderizar
        const html = `
            <div class="card">
                <h3>Total Clientes: ${totalClientes}</h3>
                <p>Tasa de Retención: ${tasaRetencion}</p>
            </div>
        `;
        
        document.getElementById('reportes-clientes').innerHTML = html;
        
    } catch (error) {
        console.error('Error:', error);
    }
}
```

#### Ejemplo 4: Cargar los 3 servicios en paralelo

```javascript
async function cargarTodosLosServicios() {
    try {
        // Hacer las 3 peticiones al mismo tiempo
        const [ventas, productos, clientes] = await Promise.all([
            fetch('http://localhost:3001/api/data').then(r => r.json()),
            fetch('http://localhost:3002/api/data').then(r => r.json()),
            fetch('http://localhost:3003/api/data').then(r => r.json())
        ]);
        
        console.log('Ventas:', ventas);
        console.log('Productos:', productos);
        console.log('Clientes:', clientes);
        
        // Usar los datos
        mostrarEstadisticas(ventas.data);
        mostrarProductos(productos.data);
        mostrarClientes(clientes.data);
        
    } catch (error) {
        console.error('Error al cargar servicios:', error);
    }
}
```

---

## 💡 Ejemplos Prácticos

### Ejemplo 1: Dashboard Simple

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>Dashboard de Ventas</h1>
        
        <div class="row mt-4">
            <!-- Card de Ventas -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Total Ventas</h5>
                        <h2 id="total-ventas" class="text-primary">Cargando...</h2>
                        <p class="text-muted" id="tendencia">-</p>
                    </div>
                </div>
            </div>
            
            <!-- Card de Productos -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Total Productos</h5>
                        <h2 id="total-productos" class="text-success">Cargando...</h2>
                        <p class="text-muted" id="bajo-stock">-</p>
                    </div>
                </div>
            </div>
            
            <!-- Card de Clientes -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Total Clientes</h5>
                        <h2 id="total-clientes" class="text-info">Cargando...</h2>
                        <p class="text-muted" id="retencion">-</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        async function cargarDashboard() {
            try {
                // Cargar los 3 servicios
                const [ventas, productos, clientes] = await Promise.all([
                    fetch('http://localhost:3001/api/data').then(r => r.json()),
                    fetch('http://localhost:3002/api/data').then(r => r.json()),
                    fetch('http://localhost:3003/api/data').then(r => r.json())
                ]);
                
                // Actualizar Ventas
                document.getElementById('total-ventas').textContent = 
                    `S/. ${ventas.data.totalVentas.toLocaleString('es-PE')}`;
                document.getElementById('tendencia').textContent = 
                    `Tendencia: ${ventas.data.tendencia}`;
                
                // Actualizar Productos
                document.getElementById('total-productos').textContent = 
                    productos.data.totalProductos;
                document.getElementById('bajo-stock').textContent = 
                    `${productos.data.productosBajoStock} bajo stock`;
                
                // Actualizar Clientes
                document.getElementById('total-clientes').textContent = 
                    clientes.data.totalClientes;
                document.getElementById('retencion').textContent = 
                    `Retención: ${clientes.data.tasaRetencion}`;
                
            } catch (error) {
                console.error('Error:', error);
                alert('Error al cargar los datos');
            }
        }
        
        // Cargar al inicio
        document.addEventListener('DOMContentLoaded', cargarDashboard);
        
        // Actualizar cada 30 segundos
        setInterval(cargarDashboard, 30000);
    </script>
</body>
</html>
```

---

### Ejemplo 2: Tabla de Productos con Datos en Tiempo Real

```html
<div class="container">
    <h2>Análisis de Inventario</h2>
    <button onclick="actualizarInventario()" class="btn btn-primary">
        Actualizar
    </button>
    
    <table class="table mt-3">
        <thead>
            <tr>
                <th>Métrica</th>
                <th>Valor</th>
            </tr>
        </thead>
        <tbody id="tabla-inventario">
            <tr><td colspan="2">Cargando...</td></tr>
        </tbody>
    </table>
</div>

<script>
    async function actualizarInventario() {
        try {
            const response = await fetch('http://localhost:3002/api/data');
            const datos = await response.json();
            const inv = datos.data;
            
            const html = `
                <tr>
                    <td>Total de Productos</td>
                    <td><strong>${inv.totalProductos}</strong></td>
                </tr>
                <tr>
                    <td>Productos Activos</td>
                    <td><span class="badge bg-success">${inv.productosActivos}</span></td>
                </tr>
                <tr>
                    <td>Productos Bajo Stock</td>
                    <td><span class="badge bg-danger">${inv.productosBajoStock}</span></td>
                </tr>
                <tr>
                    <td>Categorías</td>
                    <td>${inv.categorias}</td>
                </tr>
                <tr>
                    <td>Más Vendido</td>
                    <td><strong>${inv.masVendido}</strong></td>
                </tr>
                <tr>
                    <td>Stock Total</td>
                    <td>${inv.stockTotal} unidades</td>
                </tr>
            `;
            
            document.getElementById('tabla-inventario').innerHTML = html;
            
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // Cargar al inicio
    document.addEventListener('DOMContentLoaded', actualizarInventario);
</script>
```

---

### Ejemplo 3: Gráfico de Ventas (con Chart.js)

```html
<!DOCTYPE html>
<html>
<head>
    <title>Gráficos de Ventas</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="container">
        <canvas id="graficoVentas" width="400" height="200"></canvas>
    </div>
    
    <script>
        async function crearGraficoVentas() {
            try {
                const response = await fetch('http://localhost:3001/api/data');
                const datos = await response.json();
                const ventas = datos.data;
                
                const ctx = document.getElementById('graficoVentas').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: ['Total Ventas', 'Ventas Hoy', 'Ventas Mes'],
                        datasets: [{
                            label: 'Ventas',
                            data: [
                                ventas.totalVentas,
                                ventas.ventasHoy * 100, // Ajustar escala
                                ventas.ventasMes * 100  // Ajustar escala
                            ],
                            backgroundColor: [
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)'
                            ],
                            borderColor: [
                                'rgba(54, 162, 235, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
                
            } catch (error) {
                console.error('Error:', error);
            }
        }
        
        document.addEventListener('DOMContentLoaded', crearGraficoVentas);
    </script>
</body>
</html>
```

---

## 🎯 Casos de Uso Reales

### Caso 1: Página de Inicio con Resumen Ejecutivo

**Ubicación**: `src/main/resources/templates/client/indexclient.html`

```html
<!-- Sección de Estadísticas -->
<section class="estadisticas py-5 bg-light">
    <div class="container">
        <div class="row text-center">
            <div class="col-md-3">
                <div class="stat-box">
                    <i class="fas fa-shopping-cart fa-3x text-primary"></i>
                    <h3 id="stat-ventas" class="mt-3">0</h3>
                    <p>Ventas Realizadas</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-box">
                    <i class="fas fa-box fa-3x text-success"></i>
                    <h3 id="stat-productos" class="mt-3">0</h3>
                    <p>Productos Disponibles</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-box">
                    <i class="fas fa-users fa-3x text-info"></i>
                    <h3 id="stat-clientes" class="mt-3">0</h3>
                    <p>Clientes Satisfechos</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-box">
                    <i class="fas fa-star fa-3x text-warning"></i>
                    <h3 id="stat-retencion" class="mt-3">0%</h3>
                    <p>Tasa de Retención</p>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    async function cargarEstadisticasInicio() {
        try {
            const [ventas, productos, clientes] = await Promise.all([
                fetch('http://localhost:3001/api/data').then(r => r.json()),
                fetch('http://localhost:3002/api/data').then(r => r.json()),
                fetch('http://localhost:3003/api/data').then(r => r.json())
            ]);
            
            // Animar los números
            animarNumero('stat-ventas', 0, ventas.data.ventasMes, 2000);
            animarNumero('stat-productos', 0, productos.data.productosActivos, 2000);
            animarNumero('stat-clientes', 0, clientes.data.clientesActivos, 2000);
            
            document.getElementById('stat-retencion').textContent = 
                clientes.data.tasaRetencion;
                
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    function animarNumero(elementId, inicio, fin, duracion) {
        const elemento = document.getElementById(elementId);
        const incremento = (fin - inicio) / (duracion / 16);
        let actual = inicio;
        
        const timer = setInterval(() => {
            actual += incremento;
            if (actual >= fin) {
                elemento.textContent = Math.round(fin);
                clearInterval(timer);
            } else {
                elemento.textContent = Math.round(actual);
            }
        }, 16);
    }
    
    document.addEventListener('DOMContentLoaded', cargarEstadisticasInicio);
</script>
```

---

### Caso 2: Panel de Control de Administración

**Ubicación**: `src/main/resources/templates/gestion/gestion-dashboard.html`

```html
<div class="container-fluid">
    <h1>Panel de Control</h1>
    
    <!-- KPIs Principales -->
    <div class="row mt-4">
        <div class="col-xl-3 col-md-6">
            <div class="card border-left-primary shadow">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col">
                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                Ventas del Mes
                            </div>
                            <div class="h5 mb-0 font-weight-bold" id="kpi-ventas-mes">
                                S/. 0.00
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-calendar fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-xl-3 col-md-6">
            <div class="card border-left-success shadow">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col">
                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                Productos en Stock
                            </div>
                            <div class="h5 mb-0 font-weight-bold" id="kpi-stock">
                                0
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-boxes fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-xl-3 col-md-6">
            <div class="card border-left-info shadow">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col">
                            <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                Clientes Activos
                            </div>
                            <div class="h5 mb-0 font-weight-bold" id="kpi-clientes">
                                0
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-users fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-xl-3 col-md-6">
            <div class="card border-left-warning shadow">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col">
                            <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                Promedio de Venta
                            </div>
                            <div class="h5 mb-0 font-weight-bold" id="kpi-promedio">
                                S/. 0.00
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Alertas y Notificaciones -->
    <div class="row mt-4">
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-warning text-white">
                    <i class="fas fa-exclamation-triangle"></i> Alertas de Stock
                </div>
                <div class="card-body" id="alertas-stock">
                    Cargando...
                </div>
            </div>
        </div>
        
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-info text-white">
                    <i class="fas fa-chart-line"></i> Tendencias
                </div>
                <div class="card-body" id="tendencias">
                    Cargando...
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    async function cargarDashboardAdmin() {
        try {
            const [ventas, productos, clientes] = await Promise.all([
                fetch('http://localhost:3001/api/data').then(r => r.json()),
                fetch('http://localhost:3002/api/data').then(r => r.json()),
                fetch('http://localhost:3003/api/data').then(r => r.json())
            ]);
            
            // Actualizar KPIs
            document.getElementById('kpi-ventas-mes').textContent = 
                `S/. ${ventas.data.totalVentas.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
            
            document.getElementById('kpi-stock').textContent = 
                productos.data.stockTotal;
            
            document.getElementById('kpi-clientes').textContent = 
                clientes.data.clientesActivos;
            
            document.getElementById('kpi-promedio').textContent = 
                `S/. ${ventas.data.promedioVenta.toFixed(2)}`;
            
            // Alertas de stock
            if (productos.data.productosBajoStock > 0) {
                document.getElementById('alertas-stock').innerHTML = `
                    <div class="alert alert-warning">
                        <strong>¡Atención!</strong> Hay ${productos.data.productosBajoStock} 
                        productos con stock bajo.
                    </div>
                `;
            } else {
                document.getElementById('alertas-stock').innerHTML = `
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i> 
                        Todo el inventario está en niveles óptimos.
                    </div>
                `;
            }
            
            // Tendencias
            document.getElementById('tendencias').innerHTML = `
                <p><strong>Tendencia de Ventas:</strong> 
                    <span class="badge bg-success">${ventas.data.tendencia}</span>
                </p>
                <p><strong>Producto Más Vendido:</strong> ${productos.data.masVendido}</p>
                <p><strong>Tasa de Retención:</strong> ${clientes.data.tasaRetencion}</p>
                <p><strong>Nuevos Clientes Este Mes:</strong> ${clientes.data.nuevosEsteMes}</p>
            `;
            
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    // Cargar al inicio
    document.addEventListener('DOMContentLoaded', cargarDashboardAdmin);
    
    // Actualizar cada 1 minuto
    setInterval(cargarDashboardAdmin, 60000);
</script>
```

---

### Caso 3: Widget de Estadísticas para la Barra Lateral

```html
<!-- En gestion-sidebar.html -->
<div class="sidebar-widget">
    <h6 class="sidebar-heading">Resumen Rápido</h6>
    <ul class="list-unstyled">
        <li class="mb-2">
            <i class="fas fa-chart-line text-primary"></i>
            <span id="widget-ventas">S/. 0</span>
            <small class="text-muted d-block">Ventas Totales</small>
        </li>
        <li class="mb-2">
            <i class="fas fa-box text-success"></i>
            <span id="widget-productos">0</span>
            <small class="text-muted d-block">Productos</small>
        </li>
        <li class="mb-2">
            <i class="fas fa-users text-info"></i>
            <span id="widget-clientes">0</span>
            <small class="text-muted d-block">Clientes</small>
        </li>
    </ul>
</div>

<script>
    async function actualizarWidgetSidebar() {
        try {
            const [ventas, productos, clientes] = await Promise.all([
                fetch('http://localhost:3001/api/data').then(r => r.json()),
                fetch('http://localhost:3002/api/data').then(r => r.json()),
                fetch('http://localhost:3003/api/data').then(r => r.json())
            ]);
            
            document.getElementById('widget-ventas').textContent = 
                `S/. ${(ventas.data.totalVentas / 1000).toFixed(1)}K`;
            
            document.getElementById('widget-productos').textContent = 
                productos.data.totalProductos;
            
            document.getElementById('widget-clientes').textContent = 
                clientes.data.totalClientes;
                
        } catch (error) {
            console.error('Error en widget:', error);
        }
    }
    
    // Actualizar cada 2 minutos
    setInterval(actualizarWidgetSidebar, 120000);
    document.addEventListener('DOMContentLoaded', actualizarWidgetSidebar);
</script>
```

---

## 🔧 Personalización Avanzada

### 1. Agregar Filtros de Fecha

```javascript
async function obtenerVentasPorFecha(fechaInicio, fechaFin) {
    try {
        // Por ahora los servicios no aceptan parámetros, 
        // pero puedes filtrar en el frontend
        const response = await fetch('http://localhost:3001/api/data');
        const datos = await response.json();
        
        // Aquí podrías agregar lógica de filtrado
        // Nota: Para filtrado real, necesitarías modificar el backend Node.js
        
        return datos;
    } catch (error) {
        console.error('Error:', error);
    }
}
```

### 2. Exportar Datos a Excel

```javascript
function exportarAExcel() {
    fetch('http://localhost:3001/api/data')
        .then(r => r.json())
        .then(datos => {
            const csv = convertirACSV(datos.data);
            descargarCSV(csv, 'estadisticas-ventas.csv');
        });
}

function convertirACSV(objeto) {
    const headers = Object.keys(objeto).join(',');
    const values = Object.values(objeto).join(',');
    return `${headers}\n${values}`;
}

function descargarCSV(csv, nombreArchivo) {
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    a.click();
}
```

### 3. Notificaciones en Tiempo Real

```javascript
class NotificacionesServicio {
    constructor() {
        this.ultimosValores = {};
    }
    
    async verificarCambios() {
        try {
            const response = await fetch('http://localhost:3002/api/data');
            const datos = await response.json();
            
            // Verificar si el stock bajo ha cambiado
            if (this.ultimosValores.stockBajo !== datos.data.productosBajoStock) {
                if (datos.data.productosBajoStock > 0) {
                    this.mostrarNotificacion(
                        'Stock Bajo',
                        `Hay ${datos.data.productosBajoStock} productos con stock bajo`
                    );
                }
                this.ultimosValores.stockBajo = datos.data.productosBajoStock;
            }
            
        } catch (error) {
            console.error('Error:', error);
        }
    }
    
    mostrarNotificacion(titulo, mensaje) {
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(titulo, {
                body: mensaje,
                icon: '/images/logo.png'
            });
        } else {
            // Fallback: mostrar alerta en la página
            alert(`${titulo}: ${mensaje}`);
        }
    }
    
    iniciar() {
        // Pedir permiso para notificaciones
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
        
        // Verificar cada 5 minutos
        setInterval(() => this.verificarCambios(), 300000);
    }
}

// Usar
const notificaciones = new NotificacionesServicio();
notificaciones.iniciar();
```

### 4. Cache de Datos

```javascript
class CacheServicios {
    constructor(tiempoExpiracion = 60000) { // 1 minuto por defecto
        this.cache = new Map();
        this.tiempoExpiracion = tiempoExpiracion;
    }
    
    async obtenerDatos(url) {
        const ahora = Date.now();
        
        // Verificar si tenemos datos en caché y no han expirado
        if (this.cache.has(url)) {
            const { datos, timestamp } = this.cache.get(url);
            if (ahora - timestamp < this.tiempoExpiracion) {
                console.log('Usando datos en caché para:', url);
                return datos;
            }
        }
        
        // Si no hay caché o expiró, hacer la petición
        const response = await fetch(url);
        const datos = await response.json();
        
        // Guardar en caché
        this.cache.set(url, { datos, timestamp: ahora });
        
        return datos;
    }
    
    limpiarCache() {
        this.cache.clear();
    }
}

// Usar
const cache = new CacheServicios(60000); // Caché de 1 minuto

async function obtenerVentasConCache() {
    const datos = await cache.obtenerDatos('http://localhost:3001/api/data');
    return datos;
}
```

---

## 🚨 Solución de Problemas

### Problema 1: Error de CORS

**Síntoma**: "Access to fetch at '...' from origin '...' has been blocked by CORS policy"

**Solución**: Los servicios Node ya tienen CORS habilitado. Si persiste:

1. Verifica que accedes desde `http://localhost:8083`
2. Limpia caché del navegador
3. Usa modo incógnito para probar

### Problema 2: Servicio No Responde

**Síntoma**: `ERR_CONNECTION_REFUSED`

**Solución**:
```powershell
# Verificar que el servicio esté corriendo
docker-compose ps

# Ver logs
docker-compose logs node-service-1

# Reiniciar el servicio
docker-compose restart node-service-1
```

### Problema 3: Datos No Se Actualizan

**Síntoma**: Los datos en el frontend no cambian

**Solución**:
```javascript
// Asegúrate de que no estás usando caché del navegador
fetch('http://localhost:3001/api/data', {
    cache: 'no-cache',
    headers: {
        'Cache-Control': 'no-cache'
    }
});
```

### Problema 4: Error al Parsear JSON

**Síntoma**: `SyntaxError: Unexpected token < in JSON`

**Solución**:
```javascript
async function obtenerDatos() {
    try {
        const response = await fetch('http://localhost:3001/api/data');
        
        // Verificar que la respuesta sea OK
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        // Verificar que la respuesta sea JSON
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('La respuesta no es JSON');
        }
        
        const datos = await response.json();
        return datos;
        
    } catch (error) {
        console.error('Error al obtener datos:', error);
        return null;
    }
}
```

---

## ✅ Mejores Prácticas

### 1. Manejo de Errores

```javascript
async function obtenerDatosSeguro(url, nombreServicio) {
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            timeout: 5000 // 5 segundos
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        
        return await response.json();
        
    } catch (error) {
        console.error(`Error en ${nombreServicio}:`, error);
        
        // Mostrar mensaje al usuario
        mostrarError(`No se pudieron cargar los datos de ${nombreServicio}`);
        
        // Retornar datos por defecto
        return {
            status: 'error',
            message: error.message,
            data: null
        };
    }
}

function mostrarError(mensaje) {
    const errorDiv = document.getElementById('error-servicios');
    if (errorDiv) {
        errorDiv.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle"></i> ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
}
```

### 2. Loading States

```javascript
function mostrarCargando(mostrar) {
    const loader = document.getElementById('loading-servicios');
    if (loader) {
        loader.style.display = mostrar ? 'block' : 'none';
    }
}

async function cargarDatosConLoader() {
    mostrarCargando(true);
    
    try {
        const datos = await fetch('http://localhost:3001/api/data')
            .then(r => r.json());
        
        // Procesar datos
        mostrarDatos(datos);
        
    } catch (error) {
        console.error('Error:', error);
    } finally {
        mostrarCargando(false);
    }
}
```

### 3. Debounce para Actualización

```javascript
function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

// Usar debounce para evitar llamadas excesivas
const actualizarDatosDebounced = debounce(async function() {
    await cargarDatosServicios();
}, 1000);

// Escuchar cambios y actualizar con debounce
window.addEventListener('focus', actualizarDatosDebounced);
```

### 4. Retry Logic

```javascript
async function fetchConReintentos(url, maxReintentos = 3) {
    for (let i = 0; i < maxReintentos; i++) {
        try {
            const response = await fetch(url);
            if (response.ok) {
                return await response.json();
            }
        } catch (error) {
            console.log(`Intento ${i + 1} falló:`, error);
            if (i === maxReintentos - 1) throw error;
            
            // Esperar antes de reintentar (exponential backoff)
            await new Promise(resolve => setTimeout(resolve, 1000 * Math.pow(2, i)));
        }
    }
}
```

---

## 📝 Resumen de URLs

| Servicio | Puerto | URL Base | Endpoint de Datos |
|----------|--------|----------|-------------------|
| Service 1 - Ventas | 3001 | http://localhost:3001 | /api/data |
| Service 2 - Productos | 3002 | http://localhost:3002 | /api/data |
| Service 3 - Clientes | 3003 | http://localhost:3003 | /api/data |

---

## 🎓 Conclusión

Con esta guía tienes todo lo necesario para:

✅ Consumir los 3 servicios Node.js desde tu aplicación  
✅ Crear dashboards interactivos  
✅ Implementar actualización en tiempo real  
✅ Manejar errores correctamente  
✅ Aplicar mejores prácticas  

Los servicios están listos para usar y puedes integrarlos en cualquier parte de tu aplicación Thymeleaf.

---

**¿Necesitas ayuda?** Revisa la sección de Solución de Problemas o consulta los logs:
```powershell
docker-compose logs -f node-service-1 node-service-2 node-service-3
```

**¡Feliz codificación! 🚀**

