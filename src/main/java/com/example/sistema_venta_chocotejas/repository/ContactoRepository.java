package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    // Siempre habrá un solo registro, así que obtenemos el primero
    default Contacto obtenerContacto() {
        return findAll().stream().findFirst().orElse(null);
    }
}