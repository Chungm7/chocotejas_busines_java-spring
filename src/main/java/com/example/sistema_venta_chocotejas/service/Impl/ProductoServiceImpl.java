package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.repository.CategoriaRepository;
import com.example.sistema_venta_chocotejas.repository.ProductoRepository;
import com.example.sistema_venta_chocotejas.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodoslosProductos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto guardarProducto(Producto producto) {
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
    public List<Producto> listarProductosporCategoria() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del producto no puede ser nulo");
        }

        Producto producto = obtenerProductoPorId(id).
                orElseThrow(() -> new IllegalArgumentException("El id del producto no existe"));
        producto.setEstado(2);
        productoRepository.save(producto);
    }
}
