package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Slider;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SliderService {

    List<Slider> listarSlidersActivos();

    Optional<Slider> activarSlider(Long id);

    Optional<Slider> cambiarEstadoSlider(Long id);

    Optional<Slider> obtenerSliderPorId(Long id);

    Slider guardarSlider(Slider slider, MultipartFile file) throws IOException;

    List<Slider> sliderActivo();

    void eliminarSlider(Long id);

    Long contarSlidersActivos();

    Long contarSlidersInactivos();

    Long contarSliders();

}
