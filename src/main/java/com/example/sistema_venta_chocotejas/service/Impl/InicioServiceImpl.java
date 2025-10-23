package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Inicio;
import com.example.sistema_venta_chocotejas.repository.InicioRepository;
import com.example.sistema_venta_chocotejas.service.InicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InicioServiceImpl implements InicioService {

    private final InicioRepository inicioRepository;

    public InicioServiceImpl(InicioRepository inicioRepository) {
        this.inicioRepository = inicioRepository;
        // Inicializar el inicio si no existe
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inicio> obtenerInicio() {
        Inicio inicio = inicioRepository.obtenerInicio();
        return Optional.ofNullable(inicio);
    }

    @Override
    @Transactional
    public Inicio guardarInicio(Inicio inicio) {
        // Eliminar cualquier inicio existente (solo debe haber uno)
        inicioRepository.deleteAll();
        return inicioRepository.save(inicio);
    }

    @Override
    @Transactional
    public Inicio actualizarInicio(Inicio inicio) {
        Inicio inicioExistente = inicioRepository.obtenerInicio();

        if (inicioExistente != null) {
            // Actualizar el registro existente
            inicioExistente.setTitulo(inicio.getTitulo());
            inicioExistente.setContenido(inicio.getContenido());
            return inicioRepository.save(inicioExistente);
        } else {
            // Si no existe, crear uno nuevo
            return inicioRepository.save(inicio);
        }
    }
}