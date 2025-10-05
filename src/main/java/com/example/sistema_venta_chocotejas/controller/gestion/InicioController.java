package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Inicio;
import com.example.sistema_venta_chocotejas.service.InicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/inicio")
public class InicioController {

    private final InicioService inicioService;

    public InicioController(InicioService inicioService) {
        this.inicioService = inicioService;
    }

    @GetMapping("/mostrar")
    public String editarInicio() {
        return "gestion/gestion-inicio";
    }

    @GetMapping("/api/obtener")
    @ResponseBody
    public ResponseEntity<?> obtenerInicio() {
        return inicioService.obtenerInicio()
                .map(inicio -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", inicio);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No se encontró información de inicio");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping("/api/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarInicio(@RequestBody Inicio inicio) {
        try {
            // Validaciones básicas
            if (inicio.getTitulo() == null || inicio.getTitulo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El título es obligatorio"));
            }

            if (inicio.getContenido() == null || inicio.getContenido().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El contenido es obligatorio"));
            }

            Inicio inicioActualizado = inicioService.actualizarInicio(inicio);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Información de inicio actualizada correctamente");
            response.put("data", inicioActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar la información de inicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint para el frontend (información pública)
    @GetMapping("/api/public")
    @ResponseBody
    public ResponseEntity<?> obtenerInicioPublico() {
        return inicioService.obtenerInicio()
                .map(inicio -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);

                    // Solo enviar información necesaria para el público
                    Map<String, Object> inicioData = new HashMap<>();
                    inicioData.put("titulo", inicio.getTitulo());
                    inicioData.put("contenido", inicio.getContenido());

                    response.put("data", inicioData);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Información de inicio no disponible");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}