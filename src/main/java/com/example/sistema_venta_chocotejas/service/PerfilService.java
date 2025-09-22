package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Opcion;
import com.example.sistema_venta_chocotejas.model.Perfil;

import java.util.List;
import java.util.Optional;

public interface PerfilService {
    List<Perfil> listarPerfilesActivos();

    List<Perfil> listarTodosLosPerfiles();

    Perfil guardarPerfil(Perfil perfil);

    Optional<Perfil> obtenerPerfilPorId(Long id);

    Optional<Perfil> cambiarEstadoPerfil(Long id);

    List<Opcion> listarTodasLasOpciones();

    void eliminarPerfil(Long id);
}