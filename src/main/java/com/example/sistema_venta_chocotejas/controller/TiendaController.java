package com.example.sistema_venta_chocotejas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    @GetMapping("/mostrar")
    public String home() {
        return "client/indexclient"; // templates/cliente/index.html
    }



}

