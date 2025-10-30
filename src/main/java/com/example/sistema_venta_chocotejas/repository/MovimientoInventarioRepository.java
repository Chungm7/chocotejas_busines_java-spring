// MovimientoInventarioRepository.java
package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByOrderByFechaDesc();

    List<MovimientoInventario> findByProductoIdOrderByFechaDesc(Long productoId);

    List<MovimientoInventario> findByTipoMovimientoOrderByFechaDesc(String tipoMovimiento);

    @Query("SELECT m FROM MovimientoInventario m LEFT JOIN FETCH m.producto ORDER BY m.fecha DESC")
    List<MovimientoInventario> findAllWithProducto();
}