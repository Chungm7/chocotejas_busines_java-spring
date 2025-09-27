package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> listarProductosActivos();

    List<Producto> listarTodoslosProductos();

    Producto guardarProducto(Producto producto);

    Optional<Producto> cambiarEstadoProducto(Long id);

    Optional<Producto> obtenerProductoPorId(Long id);

    List<Producto> listarProductosporCategoria();

    void eliminarProducto(Long id);
}
