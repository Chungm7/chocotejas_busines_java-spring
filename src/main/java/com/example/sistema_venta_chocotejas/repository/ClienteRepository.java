package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEstadoNot(Integer estado);
    List<Cliente> findByEstado(Integer estado);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    Optional<Cliente> findByNumeroDocumentoAndEstadoNot(String numeroDocumento, Integer estado);

    boolean existsByNumeroDocumentoAndEstadoNotAndIdNot(String numeroDocumento, Integer estado, Long id);

    boolean existsByNumeroDocumentoAndEstadoNot(String numeroDocumento, Integer estado);
}
