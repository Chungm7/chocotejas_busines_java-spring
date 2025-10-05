package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaService {

    List<Venta> listarVentasActivas();
    Optional<Venta> obtenerVentaPorId(Long id);
    Venta registrarVenta(Venta venta);
    void eliminarVenta(Long id);
    Long contarVentasActivas();
}