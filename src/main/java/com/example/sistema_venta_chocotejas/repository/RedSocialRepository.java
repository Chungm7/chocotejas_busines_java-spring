package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.RedSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedSocialRepository extends JpaRepository<RedSocial, Long> {
    List<RedSocial> findByEstadoNot(Integer estado);
    List<RedSocial> findByEstado(Integer estado);
    Long countByEstado(Integer estado);
    Optional<RedSocial> findByNombre(String nombre);
}