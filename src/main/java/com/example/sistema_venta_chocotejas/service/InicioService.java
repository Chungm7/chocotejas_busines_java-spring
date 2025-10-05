package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Inicio;
import java.util.Optional;

public interface InicioService {
    Optional<Inicio> obtenerInicio();
    Inicio guardarInicio(Inicio inicio);
    Inicio actualizarInicio(Inicio inicio);
}