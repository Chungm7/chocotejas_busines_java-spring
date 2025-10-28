package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Opcion;
import com.example.sistema_venta_chocotejas.model.Perfil;
import com.example.sistema_venta_chocotejas.repository.OpcionRepository;
import com.example.sistema_venta_chocotejas.repository.PerfilRepository;
import com.example.sistema_venta_chocotejas.service.PerfilService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository perfilRepository;
    private final OpcionRepository opcionRepository;

    public PerfilServiceImpl(PerfilRepository perfilRepository, OpcionRepository opcionRepository) {
        this.perfilRepository = perfilRepository;
        this.opcionRepository = opcionRepository;
    }

    @Override
    public Long contarPerfilesActivos() {
        return perfilRepository.countByEstado(1);
    }

    @Override
    public Long contarPerfilesInactivos() {
        return perfilRepository.countByEstado(0);
    }

    @Override
    public Long contarPerfiles() {
        return perfilRepository.countByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> listarPerfilesActivos() {
        return perfilRepository.findByEstado(1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> listarTodosLosPerfiles() {
        return perfilRepository.findAll();
    }

    @Override
    @Transactional
    public Perfil guardarPerfil(Perfil perfil) {
        return perfilRepository.save(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Perfil> obtenerPerfilPorId(Long id) {
        return perfilRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Perfil> cambiarEstadoPerfil(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerPerfilPorId(id).map(perfil -> {
            if (perfil.getEstado() == 1) {
                perfil.setEstado(0); // Cambiar a inactivo
            } else if (perfil.getEstado() == 0) {
                perfil.setEstado(1); // Cambiar a activo
            }
            return perfilRepository.save(perfil);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Opcion> listarTodasLasOpciones() {
        return opcionRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarPerfil(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del perfil no existe");
        }
        Perfil perfil = obtenerPerfilPorId(id).orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));
        perfil.setEstado(2); // Marcar como eliminado
        perfilRepository.save(perfil);
    }
}
