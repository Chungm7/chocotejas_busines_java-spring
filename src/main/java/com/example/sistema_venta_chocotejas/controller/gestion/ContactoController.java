package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Contacto;
import com.example.sistema_venta_chocotejas.service.ContactoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/contacto")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping("/mostrar")
    public String editarContacto() {
        return "gestion/gestion-contacto";
    }

    @GetMapping("/api/obtener")
    @ResponseBody
    public ResponseEntity<?> obtenerContacto() {
        return contactoService.obtenerContacto()
                .map(contacto -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", contacto);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No se encontró información de contacto");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping("/api/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarContacto(@RequestBody Contacto contacto) {
        try {
            // Validaciones básicas
            if (contacto.getTelefono() == null || contacto.getTelefono().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El teléfono es obligatorio"));
            }

            if (contacto.getEmail() == null || contacto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El email es obligatorio"));
            }

            if (contacto.getDireccion() == null || contacto.getDireccion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La dirección es obligatoria"));
            }

            Contacto contactoActualizado = contactoService.actualizarContacto(contacto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Información de contacto actualizada correctamente");
            response.put("data", contactoActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar la información de contacto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint para el frontend (información pública)
    @GetMapping("/api/public")
    @ResponseBody
    public ResponseEntity<?> obtenerContactoPublico() {
        return contactoService.obtenerContacto()
                .map(contacto -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);

                    // Solo enviar información necesaria para el público
                    Map<String, Object> contactoData = new HashMap<>();
                    contactoData.put("telefono", contacto.getTelefono());
                    contactoData.put("direccion", contacto.getDireccion());
                    contactoData.put("email", contacto.getEmail());
                    contactoData.put("descripcion", contacto.getDescripcion());

                    response.put("data", contactoData);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Información de contacto no disponible");
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