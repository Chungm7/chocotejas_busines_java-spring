package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.MovimientoInventario;
import java.util.List;

public interface MovimientoInventarioService {

    List<MovimientoInventario> listarTodosLosMovimientos();

    List<MovimientoInventario> listarMovimientosPorProducto(Long productoId);

    MovimientoInventario registrarMovimiento(MovimientoInventario movimiento);

    void registrarMovimientoVenta(String comprobante, Long productoId, Integer cantidad);

    void registrarMovimientoStock(Long productoId, Integer cantidad, String observaciones);
}