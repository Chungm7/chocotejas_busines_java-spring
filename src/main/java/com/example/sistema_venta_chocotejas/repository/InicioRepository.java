package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Inicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InicioRepository extends JpaRepository<Inicio, Long> {
    // Siempre habrá un solo registro, así que obtenemos el primero
    default Inicio obtenerInicio() {
        return findAll().stream().findFirst().orElse(null);
    }
}