// MovimientoInventarioServiceImpl.java
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
    public void registrarMovimientoVenta(String comprobante, Long productoId, Integer cantidadVendida) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            Integer stockAnterior = producto.getStock() + cantidadVendida; // Stock antes de la venta

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("VENTA");
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setNuevoStock(producto.getStock()); // Stock después de la venta
            movimiento.setComprobante(comprobante);
            movimiento.setProducto(producto);
            movimiento.setObservaciones("Venta registrada - Comprobante: " + comprobante +
                    " | Vendido: " + cantidadVendida + " unidades");

            movimientoInventarioRepository.save(movimiento);
        }
    }

    @Override
    @Transactional
    public void registrarMovimientoStock(Long productoId, Integer nuevoStock, String observaciones) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            Integer stockAnterior = producto.getStock();

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("ACTUALIZACION_STOCK");
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setNuevoStock(nuevoStock);
            movimiento.setComprobante("ACTUALIZACION_STOCK");
            movimiento.setProducto(producto);

            String observacionesCompletas = observaciones != null ?
                    observaciones :
                    String.format("Actualización manual: Stock anterior %d, Nuevo stock %d, Diferencia %+d",
                            stockAnterior, nuevoStock, nuevoStock - stockAnterior);
            movimiento.setObservaciones(observacionesCompletas);

            movimientoInventarioRepository.save(movimiento);
        }
    }

    // Nuevo método específico para actualizaciones de stock
    @Override
    @Transactional
    public void registrarMovimientoStock(Long productoId, Integer stockAnterior, Integer nuevoStock, String observaciones) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("ACTUALIZACION_STOCK");
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setNuevoStock(nuevoStock);
            movimiento.setComprobante("ACTUALIZACION_STOCK");
            movimiento.setProducto(producto);
            movimiento.setObservaciones(observaciones);

            movimientoInventarioRepository.save(movimiento);
        }
    }
}