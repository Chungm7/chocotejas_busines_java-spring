package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.dto.*;
import com.example.sistema_venta_chocotejas.model.*;
import com.example.sistema_venta_chocotejas.repository.*;
import com.example.sistema_venta_chocotejas.service.MovimientoInventarioService;
import com.example.sistema_venta_chocotejas.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoInventarioService; // Nuevo

    public VentaServiceImpl(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            ProductoRepository productoRepository,
                            MovimientoInventarioService movimientoInventarioService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioService = movimientoInventarioService;
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

        generarComprobanteYCodigo(venta);
        Venta ventaGuardada = ventaRepository.save(venta);
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            movimientoInventarioService.registrarMovimientoVenta(
                    ventaGuardada.getCodigoVenta(),
                    detalle.getProducto().getId(),
                    detalle.getCantidad()
            );
        }
        return ventaGuardada;
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
        dto.setComprobantePago(venta.getComprobantePago()); // NUEVO
        dto.setCodigoVenta(venta.getCodigoVenta()); // NUEVO

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
    private void generarComprobanteYCodigo(Venta venta) {
        Cliente cliente = venta.getCliente();
        LocalDateTime fecha = venta.getFecha();

        // Determinar tipo de comprobante
        String comprobante = "DNI".equals(cliente.getTipoDocumento()) ? "BOLETA" : "FACTURA";
        venta.setComprobantePago(comprobante);

        // Generar código único
        String prefijo = "DNI".equals(cliente.getTipoDocumento()) ? "B" : "F";
        String documento = cliente.getNumeroDocumento();
        String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd-MM-yy-HH-mm-ss"));

        String codigoVenta = prefijo + "-" + documento + "-" + fechaFormateada;
        venta.setCodigoVenta(codigoVenta);
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