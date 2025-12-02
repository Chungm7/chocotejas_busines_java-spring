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
            promedioCompras: 4.7,
            tasaRetencion: '88.2%',
            clientesFrecuentes: 87,
            nuevosEsteMes: 24,
            clientesActivos: 289,
            totalClientes: 328,
        data: {
        message: 'Reportes de clientes obtenidos correctamente',
        status: 'success',
        serviceName: SERVICE_NAME,
    res.json({
app.get('/api/data', (req, res) => {
// Endpoint principal de datos

app.use(express.json());
app.use(cors());
// Middleware

const SERVICE_NAME = process.env.SERVICE_NAME || 'Reportes de Clientes';
const PORT = process.env.PORT || 3003;

const app = express();
const cors = require('cors');

