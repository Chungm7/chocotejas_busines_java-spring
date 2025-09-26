package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByEstadoNot(Integer estado);
}
