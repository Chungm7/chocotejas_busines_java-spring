package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Producto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> listarProductosActivos();

    List<Producto> listarProductosActivos1();

    List<Producto> listarTodoslosProductos();

    List<Producto> listarProductosActivos0();

    Producto guardarProducto(Producto producto, MultipartFile imagenFile) throws IOException;

    Optional<Producto> cambiarEstadoProducto(Long id);

    Optional<Producto> obtenerProductoPorId(Long id);

    List<Producto> listarProductosporCategoria(Long idCategoria);

    Producto actualizarStockProducto(Long idProducto, int cantidad);

    void eliminarProducto(Long id);

    Long contarProductosActivos();

    Long contarProductosInactivos();

    Long contarProductos();
}
