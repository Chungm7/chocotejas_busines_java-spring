const express = require('express');
});
    console.log(`${SERVICE_NAME} corriendo en puerto ${PORT}`);
app.listen(PORT, '0.0.0.0', () => {

});
    });
        }
            health: '/health'
            data: '/api/data',
        endpoints: {
        message: `${SERVICE_NAME} está funcionando`,
    res.json({
app.get('/', (req, res) => {
// Endpoint raíz

});
    });
        timestamp: new Date().toISOString()
        service: SERVICE_NAME,
        status: 'healthy',
    res.json({
app.get('/health', (req, res) => {
// Endpoint de salud

});
    });
        }
            ultimaActualizacion: new Date().toISOString()
            stockTotal: 1250,
            masVendido: 'Chocoteja Clásica',
            categorias: 8,
            productosBajoStock: 5,
            productosActivos: 42,
            totalProductos: 47,
        data: {
        message: 'Análisis de productos obtenido correctamente',
        status: 'success',
        serviceName: SERVICE_NAME,
    res.json({
app.get('/api/data', (req, res) => {
// Endpoint principal de datos

app.use(express.json());
app.use(cors());
// Middleware

const SERVICE_NAME = process.env.SERVICE_NAME || 'Análisis de Productos';
const PORT = process.env.PORT || 3002;

const app = express();
const cors = require('cors');

