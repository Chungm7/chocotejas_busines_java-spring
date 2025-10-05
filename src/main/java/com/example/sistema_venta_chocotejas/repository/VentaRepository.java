package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstado(Integer estado);

    @Query("SELECT v FROM Venta v WHERE v.estado = 1 ORDER BY v.fecha DESC")
    List<Venta> findVentasActivas();

    Long countByEstado(Integer estado);
}