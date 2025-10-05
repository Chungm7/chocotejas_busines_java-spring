package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Logo;
import com.example.sistema_venta_chocotejas.repository.LogoRepository;
import com.example.sistema_venta_chocotejas.service.LogoService;
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
public class LogoServiceImpl implements LogoService {

    private final LogoRepository logoRepository;

    @Value("${file.logo-dir}")
    private String uploadDir;

    public LogoServiceImpl(LogoRepository logoRepository) {
        this.logoRepository = logoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Logo> listarLogosActivos() {
        return logoRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional
    public Optional<Logo> activarLogo(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        logoRepository.desactivarTodos();
        return obtenerLogoPorId(id).map(logo -> {
            logo.setActivo(!logo.getActivo());
            return logoRepository.save(logo);
        });
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
    public Logo guardarLogo(Logo logo, MultipartFile file) throws IOException {
        if (Boolean.TRUE.equals(logo.getActivo())) {
            // Si este logo se guarda como activo, desactiva los demás
            logoRepository.desactivarTodos();
        }
        if (file != null && !file.isEmpty()) {
            // Si se está actualizando y ya existe una foto, se elimina la anterior
            if (logo.getId() != null && logo.getRuta() != null) {
                eliminarImagen(logo.getRuta());
            }
            String nombreFoto = guardarImagen(file);
            logo.setRuta(nombreFoto);
            // Aquí deberías guardar la imagen en el sistema de archivos o en un servicio de almacenamiento
            // Por simplicidad, este ejemplo no incluye esa lógica
        }
        return logoRepository.save(logo);
    }


    @Override
    @Transactional
    public Optional<Logo> cambiarEstadoLogo(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerLogoPorId(id).map(producto -> {
            if (producto.getEstado() == 1) {
                producto.setEstado(0);
            } else if (producto.getEstado() == 0) {
                producto.setEstado(1);
            }
            return logoRepository.save(producto);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Logo> obtenerLogoPorId(Long id) {
        return logoRepository.findById(id);
    }

    @Override
    public Logo logoActivo() {
        return logoRepository.findByActivoTrue();
    }

    @Override
    @Transactional
    public void eliminarLogo(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del producto no puede ser nulo");
        }

        Logo logo = obtenerLogoPorId(id).
                orElseThrow(() -> new IllegalArgumentException("El id del producto no existe"));
        logo.setEstado(2);
        logoRepository.save(logo);
    }

    @Override
    public Long contarLogosActivos() {
        return logoRepository.countByEstado(1);
    }

    @Override
    public Long contarLogosInactivos() {
        return logoRepository.countByEstado(0);
    }

    @Override
    public Long contarLogos() {
        return logoRepository.countByEstadoNot(2);
    }
}
