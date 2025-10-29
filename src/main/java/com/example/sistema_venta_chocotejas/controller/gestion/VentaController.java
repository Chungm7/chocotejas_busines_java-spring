package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.*;
import com.example.sistema_venta_chocotejas.service.Impl.ClienteServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.ProductoServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.VentaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/ventas")
public class VentaController {

    private final VentaServiceImpl ventaService;
    private final ProductoServiceImpl productoService;
    private final ClienteServiceImpl clienteService;

    public VentaController(VentaServiceImpl ventaService, ProductoServiceImpl productoService, ClienteServiceImpl clienteService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaVentas() {
        return "gestion/gestion-ventas";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarVentasActivas() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", ventaService.listarVentasActivas());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerVenta(@PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id)
                .map(venta -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", venta);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/registrar")
    @ResponseBody
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequest ventaRequest) {
        try {
            // Validaciones básicas
            if (ventaRequest.getClienteId() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Debe seleccionar un cliente"));
            }
            if (ventaRequest.getDetalles() == null || ventaRequest.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Debe agregar al menos un producto a la venta"));
            }

            // Obtener el cliente
            Cliente cliente = clienteService.obtenerClientePorId(ventaRequest.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + ventaRequest.getClienteId()));

            // Crear la venta
            Venta venta = new Venta();
            venta.setFecha(LocalDateTime.now());
            venta.setCliente(cliente);

            Double total = 0.0;

            // Procesar detalles de venta
            for (DetalleVentaRequest detalleReq : ventaRequest.getDetalles()) {
                Producto producto = productoService.obtenerProductoPorId(detalleReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleReq.getProductoId()));

                // Validar que el producto tenga precio
                if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
                    throw new RuntimeException("El producto '" + producto.getNombre() + "' no tiene un precio válido asignado");
                }

                // Validar stock
                if (producto.getStock() < detalleReq.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre() +
                            ". Stock disponible: " + producto.getStock() +
                            ", solicitado: " + detalleReq.getCantidad());
                }

                // Crear detalle de venta
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto(producto);
                detalle.setCantidad(detalleReq.getCantidad());

                venta.agregarDetalle(detalle);
                total += detalle.getSubtotal();
            }

            venta.setTotal(total);

            // Registrar venta (esto actualizará el stock automáticamente)
            Venta ventaRegistrada = ventaService.registrarVenta(venta);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Venta registrada con éxito");
            response.put("data", ventaRegistrada);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al registrar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ventaService.eliminarVenta(id);
            response.put("success", true);
            response.put("message", "Venta eliminada con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint para buscar cliente por documento
    @PostMapping("/api/buscar-cliente")
    @ResponseBody
    public ResponseEntity<?> buscarClientePorDocumento(@RequestParam String numeroDocumento) {
        Map<String, Object> response = new HashMap<>();
        try {
            var clienteOpt = clienteService.buscarPorDocumento(numeroDocumento);
            if (clienteOpt.isPresent()) {
                response.put("success", true);
                response.put("data", clienteOpt.get());
                response.put("message", "Cliente encontrado");
            } else {
                response.put("success", false);
                response.put("message", "Cliente no encontrado");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Clases DTO para la request
    public static class VentaRequest {
        private Long clienteId;
        private java.util.List<DetalleVentaRequest> detalles;

        // Getters y setters
        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
        public java.util.List<DetalleVentaRequest> getDetalles() { return detalles; }
        public void setDetalles(java.util.List<DetalleVentaRequest> detalles) { this.detalles = detalles; }
    }

    public static class DetalleVentaRequest {
        private Long productoId;
        private Integer cantidad;

        // Getters y setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    // Método auxiliar para respuestas de error
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}