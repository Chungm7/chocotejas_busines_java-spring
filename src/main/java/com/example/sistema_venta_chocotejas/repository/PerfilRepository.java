package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    List<Perfil> findByEstado(Integer estado);

    List<Perfil> findByEstadoNot(Integer estado);

    Long countByEstado(Integer estado);

    Long countByEstadoNot(Integer estado);
}
