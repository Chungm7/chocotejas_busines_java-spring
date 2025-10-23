package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Logo;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.model.Slider;
import com.example.sistema_venta_chocotejas.service.SliderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/sliders")
public class SliderController {
    private final SliderService sliderService;

    public SliderController(SliderService sliderService) {
        this.sliderService = sliderService;
    }

    @GetMapping("/listar")
    public String listarSliders() {
        return "gestion/gestion-slider";
    }

    @GetMapping("api/listar")
    @ResponseBody
    public ResponseEntity<?> listarSlider() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", sliderService.listarSlidersActivos());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerSlider(@PathVariable Long id) {
        return sliderService.obtenerSliderPorId(id)
                .map(slider -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    // Preparamos los datos para el frontend
                    Map<String, Object> sliderData = new HashMap<>();
                    sliderData.put("id", slider.getId());
                    sliderData.put("nombre", slider.getNombre());
                    sliderData.put("descripcion", slider.getDescripcion());
                    sliderData.put("estado", slider.getEstado());
                    sliderData.put("activo", slider.getActivo());
                    sliderData.put("imagen", slider.getRuta());

                    response.put("data", sliderData);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }
    // Método auxiliar para respuestas de error
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarSlider(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("imagenFile") MultipartFile imagenFile) {

        try {
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre es obligatorio"));
            }

            if (imagenFile == null || imagenFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La imagen es obligatoria"));
            }


            Slider nuevoSlider = new Slider();
            nuevoSlider.setNombre(nombre.trim());
            nuevoSlider.setDescripcion(descripcion != null ? descripcion.trim() : "Sin descripción");


            Slider sliderGuardado = sliderService.guardarSlider(nuevoSlider, imagenFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado con éxito");
            response.put("data", sliderGuardado);
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
    public ResponseEntity<?> actualizarSlider(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "rutaFile", required = false) MultipartFile rutaFile) {

        try {
            Slider slider = sliderService.obtenerSliderPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            boolean hasChanges = false;


            // Actualizar campos...
            if (nombre != null && !nombre.trim().isEmpty()) {
                slider.setNombre(nombre.trim());
                hasChanges = true;
            }

            if (descripcion != null) {
                slider.setDescripcion(descripcion.trim());
                hasChanges = true;
            }


            if (rutaFile != null && !rutaFile.isEmpty()) {
                hasChanges = true;
            }

            if (!hasChanges) {
                return ResponseEntity.badRequest().body(createErrorResponse("No se realizaron cambios"));
            }

            Slider sliderActualizado = sliderService.guardarSlider(slider, rutaFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto actualizado con éxito");
            response.put("data", sliderActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar: " + e.getMessage()));
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoSlider(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return sliderService.cambiarEstadoSlider(id)
                    .map(slider -> {
                        response.put("success", true);
                        response.put("message", slider.getEstado() == 1 ? "Slider habilitado con éxito" : "Slider deshabilitado con éxito");
                        response.put("data", slider);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Slider no encontrado");
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
    public ResponseEntity<?> eliminarSlider(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (sliderService.obtenerSliderPorId(id).isEmpty()) {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            sliderService.eliminarSlider(id);
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
    public ResponseEntity<?> cambiarSliderActivo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return sliderService.activarSlider(id)
                    .map(slider -> {
                        response.put("success", true);
                        response.put("message", slider.getActivo() ?
                                "Slider marcado como Activo" : "Slider ya no es Activo");
                        response.put("data", slider);
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
