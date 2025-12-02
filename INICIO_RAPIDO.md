# 🚀 Guía Rápida de Inicio

## Pasos para ejecutar el proyecto

### 1. Preparar el entorno

```powershell
# Clonar el repositorio (si aún no lo tienes)
git clone <url-repositorio>
cd chocotejas-bussiness

# Copiar el archivo de configuración de ejemplo
Copy-Item .env.example .env
```

### 2. Iniciar los servicios

**Opción A: Script automatizado (Recomendado)**
```powershell
.\start.ps1
```

**Opción B: Docker Compose manual**
```powershell
docker-compose up --build
```

### 3. Verificar que todo está funcionando

Abre tu navegador y visita:

- **Aplicación Principal**: http://localhost:8080
- **Integración Node**: http://localhost:8080/gestion/integracion
- **PhpMyAdmin**: http://localhost:8081

### 4. Probar los servicios Node individualmente

```powershell
# Servicio 1 - Estadísticas de Ventas
curl http://localhost:3001/api/data

# Servicio 2 - Análisis de Productos
curl http://localhost:3002/api/data

# Servicio 3 - Reportes de Clientes
curl http://localhost:3003/api/data
```

### 5. Ver logs en tiempo real

```powershell
# Todos los servicios
docker-compose logs -f

# Solo Spring Boot
docker-compose logs -f sistema-chocotejas

# Solo servicios Node
docker-compose logs -f node-service-1 node-service-2 node-service-3
```

### 6. Detener los servicios

**Opción A: Script automatizado**
```powershell
.\stop.ps1
```

**Opción B: Docker Compose manual**
```powershell
docker-compose down
```

## 🔧 Comandos útiles

### Reiniciar un servicio específico
```powershell
docker-compose restart sistema-chocotejas
docker-compose restart node-service-1
```

### Reconstruir después de cambios en el código
```powershell
docker-compose up --build sistema-chocotejas
```

### Ver servicios en ejecución
```powershell
docker-compose ps
```

### Acceder a la consola de un contenedor
```powershell
docker-compose exec sistema-chocotejas bash
docker-compose exec node-service-1 sh
```

### Limpiar todo (contenedores, volúmenes, imágenes)
```powershell
docker-compose down -v
docker system prune -a
```

## 📊 Estructura de URLs

| Descripción | URL | Puerto |
|-------------|-----|--------|
| App Principal | http://localhost:8080 | 8080 |
| Integración Node | http://localhost:8080/gestion/integracion | 8080 |
| API Service 1 | http://localhost:8080/gestion/integracion/api/service1 | 8080 |
| API Service 2 | http://localhost:8080/gestion/integracion/api/service2 | 8080 |
| API Service 3 | http://localhost:8080/gestion/integracion/api/service3 | 8080 |
| API All Services | http://localhost:8080/gestion/integracion/api/all | 8080 |
| Node Service 1 | http://localhost:3001 | 3001 |
| Node Service 2 | http://localhost:3002 | 3002 |
| Node Service 3 | http://localhost:3003 | 3003 |
| PhpMyAdmin | http://localhost:8081 | 8081 |
| MySQL | localhost:3306 | 3306 |

## 🐛 Solución de problemas

### Error: "Docker no está corriendo"
```powershell
# Inicia Docker Desktop y espera a que esté completamente iniciado
```

### Error: "Puerto ya en uso"
```powershell
# Detén los servicios que estén usando los puertos
docker-compose down

# O cambia los puertos en el archivo .env
```

### Error: "No se puede conectar a MySQL"
```powershell
# Espera unos segundos a que MySQL inicie completamente
# Verifica los logs
docker-compose logs db_mysql_chocotejas
```

### Los servicios Node no responden
```powershell
# Verifica que se construyeron correctamente
docker-compose logs node-service-1
docker-compose logs node-service-2
docker-compose logs node-service-3

# Reconstruir si es necesario
docker-compose up --build node-service-1 node-service-2 node-service-3
```

### Cambios en el código no se reflejan
```powershell
# Reconstruir la imagen
docker-compose up --build sistema-chocotejas

# O reiniciar el contenedor
docker-compose restart sistema-chocotejas
```

## 🎯 Próximos pasos

1. Accede a `/gestion/integracion` para ver la integración de servicios Node
2. Personaliza los servicios Node en `node-services/service-X/index.js`
3. Modifica la vista Thymeleaf en `templates/gestion/gestion-integracion-node.html`
4. Agrega más endpoints según tus necesidades

## 📚 Documentación adicional

- [README.md](README_NEW.md) - Documentación completa del proyecto
- [INTEGRACION_NODE.md](INTEGRACION_NODE.md) - Detalles de la integración Node.js

---

¿Tienes problemas? Revisa los logs con `docker-compose logs -f`

