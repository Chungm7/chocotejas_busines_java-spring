package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.MovimientoInventario;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.repository.MovimientoInventarioRepository;
import com.example.sistema_venta_chocotejas.repository.ProductoRepository;
import com.example.sistema_venta_chocotejas.service.MovimientoInventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;

    public MovimientoInventarioServiceImpl(MovimientoInventarioRepository movimientoInventarioRepository,
                                           ProductoRepository productoRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarTodosLosMovimientos() {
        return movimientoInventarioRepository.findAllWithProducto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientosPorProducto(Long productoId) {
        return movimientoInventarioRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
        return movimientoInventarioRepository.save(movimiento);
    }

    @Override
    @Transactional
    public void registrarMovimientoVenta(String comprobante, Long productoId, Integer cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("VENTA");
            movimiento.setCantidad(-cantidad); // Negativo porque es una salida
            movimiento.setComprobante(comprobante);
            movimiento.setProducto(productoOpt.get());
            movimiento.setObservaciones("Venta registrada - Comprobante: " + comprobante);

            movimientoInventarioRepository.save(movimiento);
        }
    }

    @Override
    @Transactional
    public void registrarMovimientoStock(Long productoId, Integer cantidad, String observaciones) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("ACTUALIZACION_STOCK");
            movimiento.setCantidad(cantidad); // Positivo o negativo dependiendo del ajuste
            movimiento.setComprobante("ACTUALIZACION_STOCK");
            movimiento.setProducto(productoOpt.get());
            movimiento.setObservaciones(observaciones != null ? observaciones : "Actualización manual de stock");

            movimientoInventarioRepository.save(movimiento);
        }
    }
}