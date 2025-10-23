package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
