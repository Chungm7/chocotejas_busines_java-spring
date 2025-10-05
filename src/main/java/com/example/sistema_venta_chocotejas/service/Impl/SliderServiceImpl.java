package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Slider;
import com.example.sistema_venta_chocotejas.repository.SliderRepository;
import com.example.sistema_venta_chocotejas.service.SliderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SliderServiceImpl implements SliderService {

    @Value("${file.slider-dir}")
    private String uploadDir;
    private final SliderRepository sliderRepository;

    public SliderServiceImpl(SliderRepository sliderRepository) {
        this.sliderRepository = sliderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Slider> listarSlidersActivos() {
        return sliderRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Slider> obtenerSliderPorId(Long id) {
        return sliderRepository.findById(id);
    }
    private String guardarImagen(MultipartFile imagenFile) throws IOException {        // Genera un nombre de archivo único para evitar colisiones
        String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaCompleta = Paths.get(uploadDir + nombreUnico);

        // Crea el directorio si no existe
        Files.createDirectories(rutaCompleta.getParent());

        // Escribe el archivo en el disco
        Files.write(rutaCompleta, imagenFile.getBytes());
        return nombreUnico;
    }


    private void eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return;
        }
        try {
            Path rutaImagen = Paths.get(uploadDir+ nombreImagen);
            Files.deleteIfExists(rutaImagen);
        } catch (IOException e) {
            System.err.println("Error al eliminar el foto: " + nombreImagen + " - " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Slider guardarSlider(Slider slider, MultipartFile files) throws IOException {
        if (files != null && !files.isEmpty()) {
            // Si se está actualizando y ya existe una foto, se elimina la anterior
            if (slider.getId() != null && slider.getRuta() != null) {
                eliminarImagen(slider.getRuta());
            }
            String nombreFoto = guardarImagen(files);
            slider.setRuta(nombreFoto);
            // Aquí deberías guardar la imagen en el sistema de archivos o en un servicio de almacenamiento
            // Por simplicidad, este ejemplo no incluye esa lógica
        }
        return sliderRepository.save(slider);
    }

    @Override
    @Transactional
    public Optional<Slider> cambiarEstadoSlider(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerSliderPorId(id).map(slider -> {
            if (slider.getEstado() == 1) {
                slider.setEstado(0);
            } else if (slider.getEstado() == 0) {
                slider.setEstado(1);
            }
            return sliderRepository.save(slider);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Slider> sliderActivo() {
        return sliderRepository.findByActivoTrueAndEstado(1);
    }

    @Override
    @Transactional
    public void eliminarSlider(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del producto no puede ser nulo");
        }

        Slider slider = obtenerSliderPorId(id).
                orElseThrow(() -> new IllegalArgumentException("El id del producto no existe"));
        slider.setEstado(2);
        sliderRepository.save(slider);
    }

    @Override
    public Long contarSlidersActivos() {
        return sliderRepository.countByEstado(1);
    }

    @Override
    public Long contarSlidersInactivos() {
        return sliderRepository.countByEstado(0);
    }

    @Override
    public Long contarSliders() {
        return sliderRepository.countByEstado(2);
    }

    @Override
    @Transactional
    public Optional<Slider> activarSlider(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerSliderPorId(id).map(slider -> {
            slider.setActivo(!slider.getActivo());
            return sliderRepository.save(slider);
        });
    }
}
