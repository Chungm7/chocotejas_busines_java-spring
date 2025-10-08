package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.service.Impl.ClienteServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/gestion/clientes")
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    public ClienteController(ClienteServiceImpl clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaClientes() {
        return "gestion/gestion-clientes";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarClientes() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", clienteService.listarClientesActivos());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(id);
        if (clienteOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", clienteOpt.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCliente(
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("numeroDocumento") String numeroDocumento,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("direccion") String direccion) {

        try {
            // Validaciones básicas
            if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre completo es obligatorio"));
            }
            if (direccion == null || direccion.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La dirección es obligatoria"));
            }

            Cliente cliente = new Cliente();
            cliente.setTipoDocumento(tipoDocumento);
            cliente.setNumeroDocumento(numeroDocumento);
            cliente.setNombreCompleto(nombreCompleto.trim());
            cliente.setDireccion(direccion.trim());
            cliente.setEstado(1); // Siempre activo al crearse

            Cliente clienteGuardado = clienteService.guardarCliente(cliente);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente creado con éxito");
            response.put("data", clienteGuardado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long id,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("numeroDocumento") String numeroDocumento,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("direccion") String direccion) {

        try {
            Optional<Cliente> resultado = clienteService.actualizarCliente(id, tipoDocumento, numeroDocumento, nombreCompleto, direccion);

            if (resultado.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Cliente actualizado con éxito");
                response.put("data", resultado.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoCliente(@PathVariable Long id) {
        try {
            Optional<Cliente> clienteOpt = clienteService.cambiarEstadoCliente(id);
            if (clienteOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", clienteOpt.get().getEstado() == 1 ? "Cliente habilitado con éxito" : "Cliente deshabilitado con éxito");
                response.put("data", clienteOpt.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Cliente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar el cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Método auxiliar para respuestas de error
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}