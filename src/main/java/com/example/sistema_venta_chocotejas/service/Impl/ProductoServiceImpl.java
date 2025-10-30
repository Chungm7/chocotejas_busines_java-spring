package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.repository.CategoriaRepository;
import com.example.sistema_venta_chocotejas.repository.ProductoRepository;
import com.example.sistema_venta_chocotejas.service.MovimientoInventarioService;
import com.example.sistema_venta_chocotejas.service.ProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoInventarioService; // Nuevo


    public ProductoServiceImpl(ProductoRepository productoRepository, MovimientoInventarioService movimientoInventarioService) {
        this.productoRepository = productoRepository;
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @Override
    public Long contarProductosActivos() {
        return productoRepository.countByEstado(1);
    }

    @Override
    public Long contarProductosInactivos() {
        return productoRepository.countByEstado(0);
    }

    @Override
    public Long contarProductos() {
        return productoRepository.countByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByEstadoNot(2);
    }

    @Override
    public List<Producto> listarProductosActivos0() {
        return productoRepository.findByEstado(0);
    }

    @Override
    public List<Producto> listarProductosActivos1() {
        return productoRepository.findByEstado(1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodoslosProductos() {
        return productoRepository.findAll();
    }

    private String guardarImagen(MultipartFile imagenFile) throws IOException {        // Genera un nombre de archivo único para evitar colisiones
        String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaCompleta = Paths.get(uploadDir + nombreUnico);

        // Crea el directorio si no existe
        Files.createDirectories(rutaCompleta.getParent());

        // Escribe el archivo en el disco
        Files.write(rutaCompleta, imagenFile.getBytes());
        return nombreUnico;
    }


    private void eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return;
        }
        try {
            Path rutaImagen = Paths.get(uploadDir+ nombreImagen);
            Files.deleteIfExists(rutaImagen);
        } catch (IOException e) {
            System.err.println("Error al eliminar el foto: " + nombreImagen + " - " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public Producto guardarProducto(Producto producto , MultipartFile imagenFile) throws IOException {
        if (imagenFile != null && !imagenFile.isEmpty()) {
            // Si se está actualizando y ya existe una foto, se elimina la anterior
            if (producto.getId() != null && producto.getImagen() != null) {
                eliminarImagen(producto.getImagen());
            }
            String nombreFoto = guardarImagen(imagenFile);
            producto.setImagen(nombreFoto);
            // Aquí deberías guardar la imagen en el sistema de archivos o en un servicio de almacenamiento
            // Por simplicidad, este ejemplo no incluye esa lógica
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Producto> cambiarEstadoProducto(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerProductoPorId(id).map(producto -> {
                if (producto.getEstado() == 1) {
                    producto.setEstado(0);
                } else if (producto.getEstado() == 0) {
                    producto.setEstado(1);
                }
                return productoRepository.save(producto);
            });
    }


    @Override
    @Transactional
    public List<Producto> listarProductosporCategoria(Long idCategoria)  {
        return productoRepository.findByCategoria_IdAndCategoria_Estado(idCategoria, 1);
    }

    @Override
    @Transactional
    public Producto actualizarStockProducto(Long idProducto, int cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + idProducto));

        int stockAnterior = producto.getStock();
        producto.setStock(cantidad);
        Producto productoActualizado = productoRepository.save(producto);

        // REGISTRAR MOVIMIENTO DE INVENTARIO CON LOS NUEVOS CAMPOS
        movimientoInventarioService.registrarMovimientoStock(
                idProducto,
                stockAnterior,
                cantidad,
                String.format("Actualización manual: Stock anterior %d, Nuevo stock %d, Diferencia %+d",
                        stockAnterior, cantidad, cantidad - stockAnterior)
        );

        return productoActualizado;
    }

    // ProductoServiceImpl.java
    @Override
    @Transactional  // <- QUITAR readOnly = true
    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del producto no puede ser nulo");
        }

        Producto producto = obtenerProductoPorId(id).
                orElseThrow(() -> new IllegalArgumentException("El id del producto no existe"));
        producto.setEstado(2);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Optional<Producto> cambiarDestacadoProducto(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerProductoPorId(id).map(producto -> {
            producto.setDestacado(!producto.getDestacado());
            return productoRepository.save(producto);
        });
    }
    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosDestacadosActivos() {
        return productoRepository.findByDestacadoTrueAndEstado(1);
    }
}
