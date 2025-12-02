const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3002;
const SERVICE_NAME = process.env.SERVICE_NAME || 'Análisis de Productos';

// Middleware
app.use(cors());
app.use(express.json());

// Endpoint principal de datos
app.get('/api/data', (req, res) => {
    res.json({
        serviceName: SERVICE_NAME,
        status: 'success',
        message: 'Análisis de productos obtenido correctamente',
        data: {
            totalProductos: 47,
            productosActivos: 42,
            productosBajoStock: 5,
            categorias: 8,
            masVendido: 'Chocoteja Clásica',
            stockTotal: 1250,
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

