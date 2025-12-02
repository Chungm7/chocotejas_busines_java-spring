# 📊 Integración de Servicios Node.js en el Dashboard

**Fecha**: 2 de Diciembre, 2025  
**Ubicación**: Dashboard de Gestión  
**Estado**: ✅ IMPLEMENTADO Y FUNCIONAL

---

## 🎯 Descripción

Los 3 servicios de Node.js han sido integrados en el **Dashboard principal** de gestión (`/gestion/dashboard/mostrar`) para proporcionar acceso rápido a reportes en tiempo real.

---

## 📍 Ubicación de la Integración

**Archivo**: `src/main/resources/templates/gestion/gestion-dashboard.html`

Los reportes se muestran en **3 tarjetas adicionales** junto con las estadísticas existentes de:
- Usuarios
- Perfiles  
- Productos
- Categorías

---

## 📊 Reportes Integrados

### 1. Estadísticas de Ventas (Service 1 - Puerto 3001)

**Ubicación en el Dashboard**: Primera tarjeta de reportes

**Datos mostrados**:
- **Total de Ventas**: Monto total en soles (S/.)
- **Ventas Hoy**: Número de ventas realizadas hoy
- **Ventas del Mes**: Total de ventas del mes actual
- **Tendencia**: Porcentaje de crecimiento/decrecimiento

**Colores**: Gradiente morado (#667eea → #764ba2)

**Icono**: Gráfico con flecha hacia arriba

---

### 2. Análisis de Productos (Service 2 - Puerto 3002)

**Ubicación en el Dashboard**: Segunda tarjeta de reportes

**Datos mostrados**:
- **Total de Productos**: Cantidad total en el sistema
- **Productos Activos**: Productos disponibles para venta
- **Bajo Stock**: Productos con inventario bajo (en rojo)
- **Más Vendido**: Nombre del producto más popular

**Colores**: Gradiente marrón (heredado de productos)

**Icono**: Cajas apiladas

---

### 3. Reportes de Clientes (Service 3 - Puerto 3003)

**Ubicación en el Dashboard**: Tercera tarjeta de reportes

**Datos mostrados**:
- **Total de Clientes**: Número total de clientes registrados
- **Clientes Activos**: Clientes con actividad reciente
- **Nuevos este Mes**: Clientes registrados en el mes (en verde)
- **Tasa de Retención**: Porcentaje de clientes que regresan

**Colores**: Gradiente rosa-rojo (#f093fb → #f5576c)

**Icono**: Grupo de personas

---

## ⚙️ Funcionamiento Técnico

### Carga Automática

Los datos se cargan automáticamente cuando:

1. **La página se carga por primera vez**
   ```javascript
   document.addEventListener('DOMContentLoaded', cargarTodosLosReportes);
   ```

2. **Cada 30 segundos** (actualización automática)
   ```javascript
   setInterval(cargarTodosLosReportes, 30000);
   ```

### Indicadores de Estado

**Durante la carga**:
- Se muestra un spinner animado
- Los valores muestran "-" temporalmente

**Si hay error**:
- El valor muestra "Error"
- El mensaje inferior muestra un ícono de advertencia con "No disponible"

**Cuando se carga correctamente**:
- Los valores se actualizan instantáneamente
- Se aplica una animación sutil de pulso

---

## 🎨 Características Visuales

### Diseño Responsivo

- **Móvil**: Las tarjetas se apilan verticalmente (col-sm-6)
- **Tablet**: 2 tarjetas por fila (col-md-6)
- **Desktop**: 3 tarjetas por fila (col-lg-4)

### Animaciones

1. **Efecto Hover**: Las tarjetas se elevan y escalan ligeramente
2. **Fade In**: Aparecen con animación de entrada
3. **Pulse**: Los valores actualizados pulsan brevemente
4. **Ícono Rotado**: Los íconos rotan al hacer hover

### Gradientes de Color

Cada tipo de reporte tiene su propio gradiente distintivo:
- Ventas: Morado vibrante
- Productos: Marrón chocolate (tema de la marca)
- Clientes: Rosa-rojo dinámico

---

## 🔧 Código JavaScript Implementado

### Estructura de las Funciones

```javascript
// Función individual para cada servicio
async function cargarEstadisticasVentas() {
    try {
        const response = await fetch('http://localhost:3001/api/data');
        const datos = await response.json();
        // Actualizar DOM
    } catch (error) {
        // Manejar error
    }
}

// Función que carga todos los servicios en paralelo
async function cargarTodosLosReportes() {
    await Promise.all([
        cargarEstadisticasVentas(),
        cargarAnalisisProductos(),
        cargarReportesClientes()
    ]);
}
```

### Manejo de Errores

Cada función tiene:
- Try-catch para capturar errores de red
- Validación de respuesta HTTP
- Mensaje de error amigable en el DOM
- Log en consola para debugging

---

## 📱 Cómo Acceder

### Opción 1: Desde el Login

1. Iniciar sesión en el sistema
2. Automáticamente redirige al Dashboard
3. Los reportes se cargan automáticamente

### Opción 2: Desde el Menú

1. Hacer clic en "Dashboard" en el menú lateral
2. URL: `http://localhost:8083/gestion/dashboard/mostrar`
3. Los reportes se muestran junto con las otras estadísticas

---

## 🔍 Verificación de Funcionamiento

### Paso 1: Verificar que los Servicios Estén Corriendo

```powershell
docker-compose ps
```

**Deberías ver**:
```
node-service-1    Up    0.0.0.0:3001->3001/tcp
node-service-2    Up    0.0.0.0:3002->3002/tcp
node-service-3    Up    0.0.0.0:3003->3003/tcp
```

### Paso 2: Verificar en el Navegador

1. Abrir `http://localhost:8083/gestion/dashboard/mostrar`
2. Buscar las 3 tarjetas de reportes
3. Verificar que muestren datos (no "Error" ni spinners permanentes)

### Paso 3: Verificar la Consola del Navegador

1. Presionar F12 para abrir DevTools
2. Ir a la pestaña "Console"
3. No debería haber errores de CORS ni de fetch
4. Debería ver los datos cargados si ejecutas: `cargarTodosLosReportes()`

---

## 🐛 Solución de Problemas

### Problema 1: Las tarjetas muestran "Error"

**Causa**: Los servicios Node no están corriendo

**Solución**:
```powershell
# Verificar estado
docker-compose ps

# Si no están corriendo, levantarlos
docker-compose up -d

# Verificar logs
docker-compose logs node-service-1 node-service-2 node-service-3
```

---

### Problema 2: Los spinners nunca desaparecen

**Causa**: Error de CORS o servicios no accesibles

**Solución**:

1. Verificar en la consola del navegador (F12)
2. Buscar errores relacionados con fetch o CORS
3. Verificar que los servicios respondan:
   ```
   http://localhost:3001/api/data
   http://localhost:3002/api/data
   http://localhost:3003/api/data
   ```
4. Si hay error de CORS, verificar que los servicios Node tengan `cors()` habilitado

---

### Problema 3: Los datos no se actualizan

**Causa**: El intervalo de actualización no está funcionando

**Solución**:

1. Verificar en la consola del navegador:
   ```javascript
   // Forzar actualización manual
   cargarTodosLosReportes();
   ```

2. Recargar la página (Ctrl + F5)

3. Verificar que no haya errores de JavaScript en la consola

---

### Problema 4: El diseño se ve roto

**Causa**: CSS no se cargó correctamente

**Solución**:

1. Limpiar caché del navegador
2. Verificar que existe el archivo: `css/gestion/gestion-dashboard.css`
3. Recargar con Ctrl + F5 (recarga forzada)

---

## 🎨 Personalización

### Cambiar el Intervalo de Actualización

Editar en `gestion-dashboard.html`:

```javascript
// Cambiar de 30 segundos a 60 segundos
setInterval(cargarTodosLosReportes, 60000);

// O desactivar la actualización automática (comentar la línea)
// setInterval(cargarTodosLosReportes, 30000);
```

### Cambiar los Colores de las Tarjetas

Editar en `gestion-dashboard.css`:

```css
/* Cambiar color de tarjeta de ventas */
.card-ventas {
    background: linear-gradient(135deg, #tu-color-1 0%, #tu-color-2 100%);
}
```

### Agregar Más Información

En el HTML, puedes agregar más elementos dentro de cada tarjeta:

```html
<div class="mt-3">
    <small class="text-muted" id="nuevo-dato">
        <i class="bi bi-info-circle"></i> Información adicional
    </small>
</div>
```

Y actualizar en JavaScript:

```javascript
document.getElementById('nuevo-dato').textContent = datos.data.nuevoCampo;
```

---

## 📊 Estructura del Código

### HTML (gestion-dashboard.html)

```
Dashboard
├── Header (con botón de menú)
├── Main Content
│   ├── Grid de Tarjetas (row g-4)
│   │   ├── Tarjeta Usuarios (si tiene permiso)
│   │   ├── Tarjeta Perfiles (si tiene permiso)
│   │   ├── Tarjeta Productos (si tiene permiso)
│   │   ├── Tarjeta Categorías (si tiene permiso)
│   │   ├── ✨ Tarjeta Ventas (Node Service 1) ✨
│   │   ├── ✨ Tarjeta Productos (Node Service 2) ✨
│   │   └── ✨ Tarjeta Clientes (Node Service 3) ✨
│   └── Mensaje de Bienvenida
└── Scripts
    ├── Bootstrap
    ├── jQuery
    ├── gestion-main.js
    └── ✨ Script de integración Node.js ✨
```

### CSS (gestion-dashboard.css)

```
Variables CSS (:root)
├── Colores de tarjetas existentes
└── (Nuevos colores en variables inline)

Estilos de Tarjetas
├── .dashboard-card (base)
├── .card-usuarios
├── .card-perfiles
├── .card-productos
├── .card-categorias
├── ✨ .card-ventas (nuevo)
├── ✨ .card-clientes (nuevo)
└── Efectos hover

Animaciones
├── fadeInUp
├── pulse
└── Transiciones
```

### JavaScript (inline en dashboard.html)

```
Funciones
├── cargarEstadisticasVentas()
├── cargarAnalisisProductos()
├── cargarReportesClientes()
└── cargarTodosLosReportes()

Event Listeners
├── DOMContentLoaded → cargar inicial
└── setInterval → actualización periódica
```

---

## ✅ Checklist de Implementación

- [x] HTML de las 3 tarjetas de reportes
- [x] JavaScript para consumir los 3 servicios
- [x] Manejo de errores y loading states
- [x] CSS para estilos y animaciones
- [x] Actualización automática cada 30 segundos
- [x] Diseño responsive
- [x] Íconos apropiados
- [x] Colores distintivos
- [x] Efectos hover
- [x] Integración con el dashboard existente

---

## 📈 Datos que Se Muestran

### Service 1: Estadísticas de Ventas
```javascript
{
  totalVentas: "S/. 15,420.50",    // Formateado con separadores
  ventasHoy: "8",
  ventasMes: "142",
  tendencia: "+12.5%"               // Con ícono de flecha
}
```

### Service 2: Análisis de Productos
```javascript
{
  totalProductos: "47",
  productosActivos: "42",
  productosBajoStock: "5",          // En rojo
  masVendido: "Chocoteja Clásica"   // Con ícono de estrella
}
```

### Service 3: Reportes de Clientes
```javascript
{
  totalClientes: "328",
  clientesActivos: "289",
  nuevosEsteMes: "24",              // En verde
  tasaRetencion: "88.2%"            // Con ícono de gráfico
}
```

---

## 🚀 Ventajas de Esta Implementación

1. ✅ **Integración Nativa**: Se ve como parte del dashboard original
2. ✅ **Actualización Automática**: No requiere recargar la página
3. ✅ **Manejo de Errores**: Muestra mensajes claros si algo falla
4. ✅ **Responsive**: Funciona en móvil, tablet y desktop
5. ✅ **Performance**: Los 3 servicios se cargan en paralelo
6. ✅ **Visual**: Colores distintivos y animaciones suaves
7. ✅ **Accesible**: Fácil de encontrar desde el dashboard principal
8. ✅ **Mantenible**: Código limpio y bien documentado

---

## 📝 Notas Técnicas

### Fetch API

Se usa `fetch()` en lugar de jQuery AJAX para:
- Mejor manejo de Promises
- Sintaxis más moderna
- Menor dependencia de librerías

### Promise.all

Los 3 servicios se cargan simultáneamente:
```javascript
await Promise.all([service1, service2, service3]);
```

Esto es **más rápido** que cargarlos secuencialmente.

### Manejo de DOM

Se usa `getElementById` para actualizar valores específicos:
- Más rápido que jQuery
- Menos overhead
- Código más directo

---

## 🎯 Próximos Pasos Posibles

Si quieres extender esta funcionalidad:

1. **Agregar gráficos**: Usar Chart.js para visualizar tendencias
2. **Exportar datos**: Botón para descargar reportes en PDF/Excel
3. **Filtros de fecha**: Permitir ver datos de rangos específicos
4. **Comparativas**: Mostrar comparación mes anterior vs actual
5. **Alertas**: Notificaciones cuando hay productos bajo stock
6. **Detalles**: Modal con información más detallada al hacer clic

---

## 📞 Soporte

Si tienes problemas:

1. Verifica que los servicios Node estén corriendo
2. Revisa la consola del navegador (F12)
3. Verifica los logs de Docker: `docker-compose logs -f`
4. Consulta la guía de solución de problemas arriba

---

## ✨ Conclusión

Los servicios de Node.js están **completamente integrados** en el dashboard principal de gestión, proporcionando **reportes en tiempo real** de forma **funcional, visual y automática**.

**Acceso directo**: `http://localhost:8083/gestion/dashboard/mostrar`

**Estado**: ✅ **FUNCIONANDO AL 100%**

---

**Implementado por**: Sistema de Gestión  
**Fecha**: 2 de Diciembre, 2025  
**Versión**: 1.0.0

