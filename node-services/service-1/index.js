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
            tendencia: '+12.5%',
            promedioVenta: 108.60,
            ventasMes: 142,
            ventasHoy: 8,
            totalVentas: 15420.50,
        data: {
        message: 'Datos de estadísticas obtenidos correctamente',
        status: 'success',
        serviceName: SERVICE_NAME,
    res.json({
app.get('/api/data', (req, res) => {
// Endpoint principal de datos

app.use(express.json());
app.use(cors());
// Middleware

const SERVICE_NAME = process.env.SERVICE_NAME || 'Estadísticas de Ventas';
const PORT = process.env.PORT || 3001;

const app = express();
const cors = require('cors');

