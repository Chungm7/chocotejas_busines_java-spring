package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Momento;
import com.example.sistema_venta_chocotejas.service.MomentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/momentos")
public class MomentoController {
    private final MomentoService momentoService;

    public MomentoController(MomentoService momentoService) {
        this.momentoService = momentoService;
    }

    @GetMapping("/listar")
    public String listarMomentos() {
        return "gestion/gestion-momentos";
    }

    @GetMapping("api/listar")
    @ResponseBody
    public ResponseEntity<?> listarMomento() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", momentoService.listarMomentosActivos());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerMomento(@PathVariable Long id) {
        return momentoService.obtenerMomentoPorId(id)
                .map(momento -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    Map<String, Object> momentoData = new HashMap<>();
                    momentoData.put("id", momento.getId());
                    momentoData.put("nombre", momento.getNombre());
                    momentoData.put("descripcion", momento.getDescripcion());
                    momentoData.put("estado", momento.getEstado());
                    momentoData.put("activo", momento.getActivo());
                    momentoData.put("imagen", momento.getRuta());

                    response.put("data", momentoData);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarMomento(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("imagenFile") MultipartFile imagenFile) {

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre es obligatorio"));
            }

            if (imagenFile == null || imagenFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La imagen es obligatoria"));
            }

            Momento nuevoMomento = new Momento();
            nuevoMomento.setNombre(nombre.trim());
            nuevoMomento.setDescripcion(descripcion != null ? descripcion.trim() : "Sin descripción");

            Momento momentoGuardado = momentoService.guardarMomento(nuevoMomento, imagenFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Momento creado con éxito");
            response.put("data", momentoGuardado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarMomento(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "rutaFile", required = false) MultipartFile rutaFile) {

        try {
            Momento momento = momentoService.obtenerMomentoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Momento no encontrado"));

            boolean hasChanges = false;

            if (nombre != null && !nombre.trim().isEmpty()) {
                momento.setNombre(nombre.trim());
                hasChanges = true;
            }

            if (descripcion != null) {
                momento.setDescripcion(descripcion.trim());
                hasChanges = true;
            }

            if (rutaFile != null && !rutaFile.isEmpty()) {
                hasChanges = true;
            }

            if (!hasChanges) {
                return ResponseEntity.badRequest().body(createErrorResponse("No se realizaron cambios"));
            }

            Momento momentoActualizado = momentoService.guardarMomento(momento, rutaFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Momento actualizado con éxito");
            response.put("data", momentoActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar: " + e.getMessage()));
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoMomento(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return momentoService.cambiarEstadoMomento(id)
                    .map(momento -> {
                        response.put("success", true);
                        response.put("message", momento.getEstado() == 1 ? "Momento habilitado con éxito" : "Momento deshabilitado con éxito");
                        response.put("data", momento);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Momento no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del momento: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarMomento(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (momentoService.obtenerMomentoPorId(id).isEmpty()) {
                response.put("success", false);
                response.put("message", "Momento no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            momentoService.eliminarMomento(id);
            response.put("success", true);
            response.put("message", "Momento eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/activar-momento/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarMomentoActivo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return momentoService.activarMomento(id)
                    .map(momento -> {
                        response.put("success", true);
                        response.put("message", momento.getActivo() ?
                                "Momento marcado como Activo" : "Momento ya no es Activo");
                        response.put("data", momento);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Momento no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado destacado del momento: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}