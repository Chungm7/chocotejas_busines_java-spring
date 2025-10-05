package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Logo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Long> {
    @Modifying
    @Query("UPDATE Logo l SET l.activo = false, l.estado = 0 WHERE l.activo = true")
    void desactivarTodos();

    List<Logo> findByEstadoNot(Integer estado);

    Long countByEstado(Integer estado);

    Long countByEstadoNot(Integer estado);

    Logo findByActivoTrue();
}
