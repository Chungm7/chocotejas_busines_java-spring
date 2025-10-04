package com.example.sistema_venta_chocotejas.controller.cliente;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/galeria")
public class GaleriaController {

    @GetMapping("/productos")
    public String productos() {
        return "client/galeria"; // templates/cliente/galeria.html
    }
}
