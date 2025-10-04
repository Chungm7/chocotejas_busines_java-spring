package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Producto;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> listarCategoriasActivas();

    List<Categoria> listarCategorias1();

    Categoria guardarCategoria(Categoria categoria);

    Optional<Categoria> obtenerCategoriaPorId(Long id);

    Optional<Categoria> cambiarEstadoCategoria(Long id);

    List<Producto> listarProductosporCategoria();

    void eliminarCategoria(Long id);

    Long contarCategoriasActivas();

    Long contarCategoriasInactivas();

    Long contarCategorias();
}
