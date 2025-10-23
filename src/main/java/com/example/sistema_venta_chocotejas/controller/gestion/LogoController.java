package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Logo;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.repository.LogoRepository;
import com.example.sistema_venta_chocotejas.service.LogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/logos")
public class LogoController {

    private final LogoService logoService;

    public LogoController(LogoService logoService, LogoRepository logoRepository) {
        this.logoService = logoService;
    }

    @GetMapping("/listar")
    public String listarLogos()  {
        return "gestion/gestion-logo";
    }

    @GetMapping("api/listar")
    @ResponseBody
    public ResponseEntity<?> listarLogo() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", logoService.listarLogosActivos());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerLogo(@PathVariable Long id) {
        return logoService.obtenerLogoPorId(id)
                .map(logo -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    // Preparamos los datos para el frontend
                    Map<String, Object> logoData = new HashMap<>();
                    logoData.put("id", logo.getId());
                    logoData.put("nombre", logo.getNombre());
                    logoData.put("descripcion", logo.getDescripcion());
                    logoData.put("ruta", logo.getRuta());
                    logoData.put("estado", logo.getEstado());
                    logoData.put("activo", logo.getEstado());

                    response.put("data", logoData);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarLogo(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("rutaFile") MultipartFile rutaFile) {

        try {
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre es obligatorio"));
            }
            if (rutaFile == null || rutaFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La imagen es obligatoria"));
            }

            Logo nuevoLogo = new Logo();
            nuevoLogo.setNombre(nombre.trim());
            nuevoLogo.setDescripcion(descripcion != null ? descripcion.trim() : "Sin descripción");


            Logo logoGuardado = logoService.guardarLogo(nuevoLogo, rutaFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado con éxito");
            response.put("data", logoGuardado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    @PostMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarLogo(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "rutaFile", required = false) MultipartFile rutaFile) {

        try {
            Logo logo = logoService.obtenerLogoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            boolean hasChanges = false;


            // Actualizar campos...
            if (nombre != null && !nombre.trim().isEmpty()) {
                logo.setNombre(nombre.trim());
                hasChanges = true;
            }

            if (descripcion != null) {
                logo.setDescripcion(descripcion.trim());
                hasChanges = true;
            }


            if (rutaFile != null && !rutaFile.isEmpty()) {
                hasChanges = true;
            }

            if (!hasChanges) {
                return ResponseEntity.badRequest().body(createErrorResponse("No se realizaron cambios"));
            }

            Logo logoActualizado = logoService.guardarLogo(logo, rutaFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto actualizado con éxito");
            response.put("data", logoActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar: " + e.getMessage()));
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoLogo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return logoService.cambiarEstadoLogo(id)
                    .map(producto -> {
                        response.put("success", true);
                        response.put("message", producto.getEstado() == 1 ? "Logo habilitado con éxito" : "Logo deshabilitado con éxito");
                        response.put("data", producto);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Logo no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del logo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarLogo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (logoService.obtenerLogoPorId(id).isEmpty()) {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            logoService.eliminarLogo(id);
            response.put("success", true);
            response.put("message", "Producto eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/activar-logo/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarLogoActivo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return logoService.activarLogo(id)
                    .map(logo -> {
                        response.put("success", true);
                        response.put("message", logo.getActivo() ?
                                "Producto marcado como Activo" : "Producto ya no es Activo");
                        response.put("data", logo);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Producto no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado destacado del producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
