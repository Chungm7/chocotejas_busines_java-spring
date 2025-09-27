package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstadoNot(Integer estado);

    List<Producto> findByCategoria_IdAndCategoria_Estado(Long idCategoria, Integer estado);

    List<Producto> findByEstado(Integer estado);
}
