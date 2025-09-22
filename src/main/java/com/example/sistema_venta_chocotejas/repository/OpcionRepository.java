package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
}