package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Momento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MomentoRepository extends JpaRepository<Momento, Long> {

    List<Momento> findByEstadoNot(Integer estado);

    Long countByEstado(Integer estado);

    Long countByEstadoNot(Integer estado);

    List<Momento> findByActivoTrueAndEstado(Integer estado);
}