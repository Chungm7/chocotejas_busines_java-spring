package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEstadoNot(Integer estado);
    List<Cliente> findByEstado(Integer estado);
    Optional<Cliente> findByNumeroDocumentoAndEstadoNot(String numeroDocumento, Integer estado);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.numeroDocumento = :numeroDocumento AND c.estado != :estado AND c.id != :id")
    boolean existsByNumeroDocumentoAndEstadoNotAndIdNot(@Param("numeroDocumento") String numeroDocumento,
                                                        @Param("estado") Integer estado,
                                                        @Param("id") Long id);

    boolean existsByNumeroDocumentoAndEstadoNot(String numeroDocumento, Integer estado);
}