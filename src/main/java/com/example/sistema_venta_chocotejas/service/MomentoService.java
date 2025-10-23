package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Momento;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MomentoService {
    List<Momento> listarMomentosActivos();

    Optional<Momento> activarMomento(Long id);

    Optional<Momento> cambiarEstadoMomento(Long id);

    Optional<Momento> obtenerMomentoPorId(Long id);

    Momento guardarMomento(Momento momento, MultipartFile file) throws IOException;

    List<Momento> momentoActivo();

    void eliminarMomento(Long id);

    Long contarMomentosActivos();

    Long contarMomentosInactivos();

    Long contarMomentos();
}