package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.model.Venta;
import com.example.sistema_venta_chocotejas.repository.VentaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gestion/deudas")
public class DeudaController {

    private final VentaRepository ventaRepository;

    public DeudaController(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/listar")
    public String mostrarPaginaDeudas() {
        return "gestion/gestion-deudas";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarDeudasPendientes() {
        List<Venta> deudas = ventaRepository.findByEstadoPago("Pendiente");
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", deudas);
        return ResponseEntity.ok(response);
    }
}
