package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.dto.*;
import com.example.sistema_venta_chocotejas.model.*;
import com.example.sistema_venta_chocotejas.repository.*;
import com.example.sistema_venta_chocotejas.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<VentaDTO> listarVentasActivas() {
        List<Venta> ventas = ventaRepository.findVentasActivas();
        return ventas.stream().map(this::convertirAVentaDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaDTO> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).map(this::convertirAVentaDTO);
    }

    @Override
    @Transactional
    public Venta registrarVenta(Venta venta) {
        // Validar stock y actualizar productos
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getProducto();

            // Validaciones de seguridad
            if (producto == null) {
                throw new IllegalArgumentException("Producto no puede ser null en el detalle de venta");
            }

            if (detalle.getPrecioUnitario() == null) {
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

    // Métodos de conversión a DTO
    private VentaDTO convertirAVentaDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setEstado(venta.getEstado());

        // Convertir cliente
        if (venta.getCliente() != null) {
            dto.setCliente(convertirAClienteDTO(venta.getCliente()));
        }

        // Convertir detalles de venta
        if (venta.getDetalleVentas() != null) {
            List<DetalleVentaDTO> detallesDTO = venta.getDetalleVentas().stream()
                    .map(this::convertirADetalleVentaDTO)
                    .collect(Collectors.toList());
            dto.setDetalleVentas(detallesDTO);
        }

        return dto;
    }

    private ClienteDTO convertirAClienteDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setTipoDocumento(cliente.getTipoDocumento());
        dto.setNumeroDocumento(cliente.getNumeroDocumento());
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setDireccion(cliente.getDireccion());
        dto.setEstado(cliente.getEstado());
        return dto;
    }

    private DetalleVentaDTO convertirADetalleVentaDTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());

        // Convertir producto
        if (detalle.getProducto() != null) {
            dto.setProducto(convertirAProductoDTO(detalle.getProducto()));
        }

        return dto;
    }

    private ProductoDTO convertirAProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagen(producto.getImagen());
        dto.setEstado(producto.getEstado());
        dto.setDestacado(producto.getDestacado());
        return dto;
    }
}