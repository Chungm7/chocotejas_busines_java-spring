package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Momento;
import com.example.sistema_venta_chocotejas.repository.MomentoRepository;
import com.example.sistema_venta_chocotejas.service.MomentoService;
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
public class MomentoServiceImpl implements MomentoService {

    @Value("${file.momentos-dir}")
    private String uploadDir;
    private final MomentoRepository momentoRepository;

    public MomentoServiceImpl(MomentoRepository momentoRepository) {
        this.momentoRepository = momentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Momento> listarMomentosActivos() {
        return momentoRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Momento> obtenerMomentoPorId(Long id) {
        return momentoRepository.findById(id);
    }

    private String guardarImagen(MultipartFile imagenFile) throws IOException {
        String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaCompleta = Paths.get(uploadDir + nombreUnico);

        Files.createDirectories(rutaCompleta.getParent());
        Files.write(rutaCompleta, imagenFile.getBytes());
        return nombreUnico;
    }

    private void eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return;
        }
        try {
            Path rutaImagen = Paths.get(uploadDir + nombreImagen);
            Files.deleteIfExists(rutaImagen);
        } catch (IOException e) {
            System.err.println("Error al eliminar la imagen: " + nombreImagen + " - " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Momento guardarMomento(Momento momento, MultipartFile files) throws IOException {
        if (files != null && !files.isEmpty()) {
            if (momento.getId() != null && momento.getRuta() != null) {
                eliminarImagen(momento.getRuta());
            }
            String nombreFoto = guardarImagen(files);
            momento.setRuta(nombreFoto);
        }
        return momentoRepository.save(momento);
    }

    @Override
    @Transactional
    public Optional<Momento> cambiarEstadoMomento(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerMomentoPorId(id).map(momento -> {
            if (momento.getEstado() == 1) {
                momento.setEstado(0);
            } else if (momento.getEstado() == 0) {
                momento.setEstado(1);
            }
            return momentoRepository.save(momento);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Momento> momentoActivo() {
        return momentoRepository.findByActivoTrueAndEstado(1);
    }

    @Override
    @Transactional
    public void eliminarMomento(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del momento no puede ser nulo");
        }

        Momento momento = obtenerMomentoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("El id del momento no existe"));
        momento.setEstado(2);
        momentoRepository.save(momento);
    }

    @Override
    public Long contarMomentosActivos() {
        return momentoRepository.countByEstado(1);
    }

    @Override
    public Long contarMomentosInactivos() {
        return momentoRepository.countByEstado(0);
    }

    @Override
    public Long contarMomentos() {
        return momentoRepository.countByEstado(2);
    }

    @Override
    @Transactional
    public Optional<Momento> activarMomento(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerMomentoPorId(id).map(momento -> {
            momento.setActivo(!momento.getActivo());
            return momentoRepository.save(momento);
        });
    }
}