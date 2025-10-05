package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Contacto;
import com.example.sistema_venta_chocotejas.repository.ContactoRepository;
import com.example.sistema_venta_chocotejas.service.ContactoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ContactoServiceImpl implements ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoServiceImpl(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
        // Inicializar el contacto si no existe
        this.inicializarContacto();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contacto> obtenerContacto() {
        Contacto contacto = contactoRepository.obtenerContacto();
        return Optional.ofNullable(contacto);
    }

    @Override
    @Transactional
    public Contacto guardarContacto(Contacto contacto) {
        // Eliminar cualquier contacto existente (solo debe haber uno)
        contactoRepository.deleteAll();
        return contactoRepository.save(contacto);
    }

    @Override
    @Transactional
    public Contacto actualizarContacto(Contacto contacto) {
        Contacto contactoExistente = contactoRepository.obtenerContacto();

        if (contactoExistente != null) {
            // Actualizar el registro existente
            contactoExistente.setTelefono(contacto.getTelefono());
            contactoExistente.setDireccion(contacto.getDireccion());
            contactoExistente.setEmail(contacto.getEmail());
            contactoExistente.setDescripcion(contacto.getDescripcion());
            return contactoRepository.save(contactoExistente);
        } else {
            // Si no existe, crear uno nuevo
            return contactoRepository.save(contacto);
        }
    }

}