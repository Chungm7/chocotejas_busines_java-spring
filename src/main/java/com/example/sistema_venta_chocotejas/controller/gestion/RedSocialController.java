package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.RedSocial;
import com.example.sistema_venta_chocotejas.service.RedSocialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/redes-sociales")
public class RedSocialController {

    private final RedSocialService redSocialService;

    public RedSocialController(RedSocialService redSocialService) {
        this.redSocialService = redSocialService;
    }

    @GetMapping("/listar")
    public String listarRedesSociales() {
        return "gestion/gestion-redsocial";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarRedesSocialesAPI() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", redSocialService.listarRedesSocialesActivas());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerRedSocial(@PathVariable Long id) {
        return redSocialService.obtenerRedSocialPorId(id)
                .map(redSocial -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", redSocial);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarRedSocial(@RequestBody RedSocial redSocial) {
        try {
            RedSocial redSocialGuardada = redSocialService.guardarRedSocial(redSocial);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Red social guardada con éxito");
            response.put("data", redSocialGuardada);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarRedSocial(@PathVariable Long id, @RequestBody RedSocial redSocial) {
        try {
            if (!redSocialService.obtenerRedSocialPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }

            redSocial.setId(id);
            RedSocial redSocialActualizada = redSocialService.guardarRedSocial(redSocial);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Red social actualizada con éxito");
            response.put("data", redSocialActualizada);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoRedSocial(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return redSocialService.cambiarEstadoRedSocial(id)
                    .map(redSocial -> {
                        response.put("success", true);
                        response.put("message", redSocial.getEstado() == 1 ?
                                "Red social activada" : "Red social desactivada");
                        response.put("data", redSocial);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Red social no encontrada");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarRedSocial(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!redSocialService.obtenerRedSocialPorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Red social no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            redSocialService.eliminarRedSocial(id);
            response.put("success", true);
            response.put("message", "Red social eliminada con éxito");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/contadores")
    @ResponseBody
    public ResponseEntity<?> obtenerContadores() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
                "activas", redSocialService.contarRedesSocialesActivas(),
                "inactivas", redSocialService.contarRedesSocialesInactivas()
        ));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}