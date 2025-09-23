package com.example.sistema_venta_chocotejas.dto;

import com.example.sistema_venta_chocotejas.model.Usuario;

public record AuthenticationResponse(String token, Usuario user) {
}
