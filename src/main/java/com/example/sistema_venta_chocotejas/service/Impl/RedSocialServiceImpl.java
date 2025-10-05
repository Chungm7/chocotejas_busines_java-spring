package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.RedSocial;
import com.example.sistema_venta_chocotejas.repository.RedSocialRepository;
import com.example.sistema_venta_chocotejas.service.RedSocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RedSocialServiceImpl implements RedSocialService {

    private final RedSocialRepository redSocialRepository;

    public RedSocialServiceImpl(RedSocialRepository redSocialRepository) {
        this.redSocialRepository = redSocialRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RedSocial> listarRedesSocialesActivas() {
        return redSocialRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RedSocial> obtenerRedSocialPorId(Long id) {
        return redSocialRepository.findById(id);
    }

    @Override
    @Transactional
    public RedSocial guardarRedSocial(RedSocial redSocial) {
        // Validar unicidad del nombre
        Optional<RedSocial> existente = redSocialRepository.findByNombre(redSocial.getNombre());
        if (existente.isPresent() && !existente.get().getId().equals(redSocial.getId())) {
            throw new IllegalArgumentException("Ya existe una red social con ese nombre");
        }

        // Validar unicidad del ícono
        List<RedSocial> conMismoIcono = redSocialRepository.findByEstadoNot(2);
        for (RedSocial rs : conMismoIcono) {
            if (rs.getIcono().equals(redSocial.getIcono()) && !rs.getId().equals(redSocial.getId())) {
                throw new IllegalArgumentException("Ya existe una red social con ese ícono");
            }
        }

        return redSocialRepository.save(redSocial);
    }

    @Override
    @Transactional
    public Optional<RedSocial> cambiarEstadoRedSocial(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerRedSocialPorId(id).map(redSocial -> {
            if (redSocial.getEstado() == 1) {
                redSocial.setEstado(0);
            } else if (redSocial.getEstado() == 0) {
                redSocial.setEstado(1);
            }
            return redSocialRepository.save(redSocial);
        });
    }

    @Override
    @Transactional
    public void eliminarRedSocial(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la red social no puede ser nulo");
        }

        RedSocial redSocial = obtenerRedSocialPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("La red social no existe"));
        redSocial.setEstado(2);
        redSocialRepository.save(redSocial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RedSocial> listarRedesSocialesActivasFrontend() {
        return redSocialRepository.findByEstado(1);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarRedesSocialesActivas() {
        return redSocialRepository.countByEstado(1);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarRedesSocialesInactivas() {
        return redSocialRepository.countByEstado(0);
    }

    @Override
    @Transactional
    public Optional<RedSocial> activarRedSocial(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return obtenerRedSocialPorId(id).map(redSocial -> {
            redSocial.setEstado(1);
            return redSocialRepository.save(redSocial);
        });
    }
}