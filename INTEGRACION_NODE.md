# Integración de Servicios Node.js con Spring Boot

## 📋 Descripción

Este proyecto integra 3 servicios Node.js con la aplicación Spring Boot usando **WebClient** para consumir APIs REST de forma asíncrona.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────┐
│   Spring Boot + Thymeleaf          │
│   (Puerto configurado en .env)      │
│                                     │
│   ┌──────────────────────┐         │
│   │  WebClient           │         │
│   │  (Consumidor)        │         │
│   └──────────────────────┘         │
└──────────┬──────────────────────────┘
           │
           ├───────────► Node Service 1 (Puerto 3001)
           │             Estadísticas de Ventas
           │
           ├───────────► Node Service 2 (Puerto 3002)
           │             Análisis de Productos
           │
           └───────────► Node Service 3 (Puerto 3003)
                         Reportes de Clientes
```

## 🚀 Servicios Node Creados

### Servicio 1: Estadísticas de Ventas
- **Puerto**: 3001
- **Endpoint**: `/api/data`
- **Datos**: Total ventas, ventas hoy, ventas mes, promedio, tendencia

### Servicio 2: Análisis de Productos
- **Puerto**: 3002
- **Endpoint**: `/api/data`
- **Datos**: Total productos, activos, bajo stock, categorías, más vendido

### Servicio 3: Reportes de Clientes
- **Puerto**: 3003
- **Endpoint**: `/api/data`
- **Datos**: Total clientes, activos, nuevos, frecuentes, tasa retención

## 📁 Estructura de Archivos Creados

### Backend (Java/Spring Boot)
```
src/main/java/com/example/sistema_venta_chocotejas/
├── config/
│   └── WebClientConfig.java           # Configuración de WebClient
├── dto/
│   └── NodeServiceResponse.java       # DTO para respuestas Node
├── service/
│   └── NodeIntegrationService.java    # Servicio que consume APIs Node
└── controller/gestion/
    └── IntegracionNodeController.java # Controlador de integración
```

### Frontend (Thymeleaf)
```
src/main/resources/
├── templates/gestion/
│   └── gestion-integracion-node.html  # Vista de integración
└── application.yml                     # Configuración URLs servicios
```

### Servicios Node
```
node-services/
├── service-1/
│   ├── index.js
│   ├── package.json
│   ├── Dockerfile
│   └── .dockerignore
├── service-2/
│   ├── index.js
│   ├── package.json
│   ├── Dockerfile
│   └── .dockerignore
└── service-3/
    ├── index.js
    ├── package.json
    ├── Dockerfile
    └── .dockerignore
```

## 🔧 Configuración

### 1. Variables de Entorno (application.yml)
```yaml
node:
  service1:
    url: ${NODE_SERVICE1_URL:http://node-service-1:3001}
  service2:
    url: ${NODE_SERVICE2_URL:http://node-service-2:3002}
  service3:
    url: ${NODE_SERVICE3_URL:http://node-service-3:3003}
```

### 2. Docker Compose
Los servicios Node se agregaron al `docker-compose.yml` y se levantarán automáticamente.

## 🎯 Endpoints Disponibles

### Vistas (Thymeleaf)
- `GET /gestion/integracion` - Página principal con los 3 servicios

### API REST
- `GET /gestion/integracion/api/service1` - Solo servicio 1
- `GET /gestion/integracion/api/service2` - Solo servicio 2
- `GET /gestion/integracion/api/service3` - Solo servicio 3
- `GET /gestion/integracion/api/all` - Todos los servicios

## 🚀 Cómo Ejecutar

### Opción 1: Con Docker Compose (Recomendado)

```powershell
# Construir y levantar todos los servicios
docker-compose up --build

# Levantar en segundo plano
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### Opción 2: Desarrollo Local

#### Backend (Spring Boot)
```powershell
.\mvnw spring-boot:run
```

#### Servicios Node (en terminales separadas)
```powershell
# Servicio 1
cd node-services/service-1
npm install
npm start

# Servicio 2
cd node-services/service-2
npm install
npm start

# Servicio 3
cd node-services/service-3
npm install
npm start
```

## 🧪 Probar la Integración

### 1. Verificar servicios Node están corriendo
```powershell
# Servicio 1
curl http://localhost:3001/health

# Servicio 2
curl http://localhost:3002/health

# Servicio 3
curl http://localhost:3003/health
```

### 2. Acceder a la vista integrada
```
http://localhost:{PUERTO_SPRING}/gestion/integracion
```

### 3. Probar endpoints API
```powershell
curl http://localhost:{PUERTO_SPRING}/gestion/integracion/api/all
```

## 📊 Características Implementadas

✅ **Consumo Asíncrono**: WebClient con Reactor para peticiones no bloqueantes
✅ **Manejo de Errores**: Timeout de 5 segundos y fallback en caso de error
✅ **Peticiones Concurrentes**: Los 3 servicios se consultan en paralelo con `Mono.zip`
✅ **Docker Compose**: Todos los servicios levantados con un solo comando
✅ **Vista Interactiva**: Interfaz Thymeleaf con Bootstrap 5 mostrando datos en tiempo real
✅ **API REST**: Endpoints para consumir servicios individualmente o todos juntos
✅ **CORS Habilitado**: Servicios Node configurados para aceptar peticiones externas
✅ **Health Checks**: Endpoints de salud en cada servicio Node

## 🔄 Flujo de Datos

1. Usuario accede a `/gestion/integracion`
2. Controller llama a `NodeIntegrationService.getAllServicesData()`
3. Service hace 3 peticiones HTTP concurrentes a servicios Node
4. WebClient obtiene respuestas en paralelo con timeout de 5s
5. Datos se agregan en un Map y se pasan al Model de Thymeleaf
6. Vista renderiza los datos de los 3 servicios

## 🛠️ Tecnologías Utilizadas

### Backend
- Spring Boot 3.5.6
- Spring WebFlux (WebClient)
- Reactor Netty
- Thymeleaf
- Java 21

### Frontend
- Thymeleaf
- Bootstrap 5.3.3
- Bootstrap Icons
- JavaScript (Vanilla)

### Servicios Node
- Node.js 18 Alpine
- Express.js 4.18.2
- CORS 2.8.5

### Infraestructura
- Docker
- Docker Compose

## 📝 Notas Importantes

1. **Timeout**: Las peticiones tienen un timeout de 5 segundos configurado
2. **Manejo de Errores**: Si un servicio falla, devuelve un objeto con status "error"
3. **CORS**: Los servicios Node tienen CORS habilitado para desarrollo
4. **Docker Network**: Los servicios se comunican a través de la red de Docker

## 🔐 Seguridad

Para producción, considera:
- [ ] Agregar autenticación entre servicios (JWT, API Keys)
- [ ] Implementar rate limiting
- [ ] Configurar HTTPS
- [ ] Validar y sanitizar datos recibidos
- [ ] Agregar logging y monitoreo

## 📚 Próximos Pasos

- [ ] Agregar caché para reducir llamadas a servicios Node
- [ ] Implementar circuit breaker con Resilience4j
- [ ] Agregar métricas con Micrometer
- [ ] Crear tests unitarios e integración
- [ ] Implementar WebSockets para actualizaciones en tiempo real

---

**Autor**: Sistema de Venta de Chocotejas
**Fecha**: Diciembre 2025

