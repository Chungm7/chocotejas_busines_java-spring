package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.repository.CategoriaRepository;
import com.example.sistema_venta_chocotejas.repository.ProductoRepository;
import com.example.sistema_venta_chocotejas.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaIServicempl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaIServicempl(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }


    @Override
    @Transactional
    public Long contarCategoriasActivas() {
        return categoriaRepository.countByEstado(1);
    }

    @Override
    @Transactional
    public Long contarCategoriasInactivas() {
        return categoriaRepository.countByEstado(0);
    }

    @Override
    @Transactional
    public Long contarCategorias() {
        return categoriaRepository.countByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarCategoriasActivas() {
        return categoriaRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodaslasCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Categoria> cambiarEstadoCategoria(Long id) {
        if(id ==null || id <=0){
            return Optional.empty();
        }
        return obtenerCategoriaPorId(id).map(categoria -> {
            if(categoria.getEstado() ==1) {
                categoria.setEstado(0);
            } else if (categoria.getEstado()==0) {
                categoria.setEstado(1);
            }
            return  categoriaRepository.save(categoria);
        });
    }

    @Override
    @Transactional
    public List<Producto> listarProductosporCategoria() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        if (id ==null || id <=0) {
            throw new IllegalArgumentException("El id del categoria no existe");
        }

        Categoria categoria = obtenerCategoriaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

        categoria.setEstado(2);
        categoriaRepository.save(categoria);
    }
}
