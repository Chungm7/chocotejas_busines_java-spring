package com.example.sistema_venta_chocotejas.repository;

import com.example.sistema_venta_chocotejas.model.Logo;
import com.example.sistema_venta_chocotejas.model.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SliderRepository  extends JpaRepository<Slider, Long> {
    List<Slider> findByEstadoNot(Integer estado);

    Long countByEstado(Integer estado);

    Long countByEstadoNot(Integer estado);

    List<Slider> findByActivoTrueAndEstado(Integer estado);
}
