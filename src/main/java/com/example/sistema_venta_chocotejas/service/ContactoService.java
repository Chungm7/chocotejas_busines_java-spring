package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Contacto;
import java.util.Optional;

public interface ContactoService {
    Optional<Contacto> obtenerContacto();
    Contacto guardarContacto(Contacto contacto);
    Contacto actualizarContacto(Contacto contacto);
}