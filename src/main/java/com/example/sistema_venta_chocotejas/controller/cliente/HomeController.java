package com.example.sistema_venta_chocotejas.controller.cliente;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/tienda/inicio"; // Redirige a la tienda por defecto
    }
}