package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gestion/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/api/registrar")
    @ResponseBody
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest pagoRequest) {
        try {
            pagoService.registrarPago(pagoRequest.getVentaId(), pagoRequest.getMonto());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago registrado con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al registrar el pago: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // DTO para la request
    public static class PagoRequest {
        private Long ventaId;
        private Double monto;

        // Getters y setters
        public Long getVentaId() { return ventaId; }
        public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

        public Double getMonto() { return monto; }
        public void setMonto(Double monto) { this.monto = monto; }
    }
}
