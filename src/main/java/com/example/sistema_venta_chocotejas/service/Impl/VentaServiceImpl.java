package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.DetalleVenta;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.model.Venta;
import com.example.sistema_venta_chocotejas.repository.DetalleVentaRepository;
import com.example.sistema_venta_chocotejas.repository.ProductoRepository;
import com.example.sistema_venta_chocotejas.repository.VentaRepository;
import com.example.sistema_venta_chocotejas.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentasActivas() {
        return ventaRepository.findVentasActivas();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    @Transactional
    public Venta registrarVenta(Venta venta) {
        // Validar stock y actualizar productos
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getProducto();

            // Validar stock disponible
            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: " + producto.getNombre() +
                                ". Stock disponible: " + producto.getStock() +
                                ", solicitado: " + detalle.getCantidad()
                );
            }

            // Actualizar stock del producto
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // Restaurar stock de productos
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        // Cambiar estado a eliminado (0)
        venta.setEstado(0);
        ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarVentasActivas() {
        return ventaRepository.countByEstado(1);
    }
}