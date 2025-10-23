package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Logo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface LogoService {
    List<Logo> listarLogosActivos();

    Optional<Logo> activarLogo(Long id);

    Logo guardarLogo(Logo logo, MultipartFile imagenFile)throws IOException;

    Optional<Logo> cambiarEstadoLogo(Long id);

    Optional<Logo> obtenerLogoPorId(Long id);

    Logo logoActivo();

    void eliminarLogo(Long id);

    Long contarLogosActivos();

    Long contarLogosInactivos();

    Long contarLogos();

}
