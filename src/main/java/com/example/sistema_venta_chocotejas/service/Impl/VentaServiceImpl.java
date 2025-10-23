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

import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.repository.ClienteRepository;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            ProductoRepository productoRepository, ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
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
    public Venta registrarVenta(Venta venta, Long clienteId, String tipoPago) {
        // Asignar cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        venta.setCliente(cliente);

        // Establecer tipo de pago y estado
        venta.setTipoPago(tipoPago);
        if ("Contado".equalsIgnoreCase(tipoPago)) {
            venta.setEstadoPago("Pagado");
            venta.setMontoPagado(venta.getTotal());
        } else {
            venta.setEstadoPago("Pendiente");
            venta.setMontoPagado(0.0);
        }

        // Validar stock y actualizar productos
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getProducto();

            // Validaciones de seguridad
            if (producto == null) {
                throw new IllegalArgumentException("Producto no puede ser null en el detalle de venta");
            }

            if (detalle.getPrecioUnitario() == null) {
                // Si por alguna razón el precio unitario es null, usar el precio del producto
                detalle.setPrecioUnitario(producto.getPrecio() != null ? producto.getPrecio() : 0.0);
            }

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
            if (producto != null) {
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
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