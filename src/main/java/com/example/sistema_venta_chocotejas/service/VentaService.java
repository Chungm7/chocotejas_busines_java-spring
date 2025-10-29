package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.dto.VentaDTO;
import com.example.sistema_venta_chocotejas.model.Venta;
import java.util.List;
import java.util.Optional;

public interface VentaService {

    List<VentaDTO> listarVentasActivas();
    Optional<VentaDTO> obtenerVentaPorId(Long id);
    Venta registrarVenta(Venta venta);
    void eliminarVenta(Long id);
    Long contarVentasActivas();
}