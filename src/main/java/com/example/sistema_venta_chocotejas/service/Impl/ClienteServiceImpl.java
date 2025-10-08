package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.repository.ClienteRepository;
import com.example.sistema_venta_chocotejas.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        // Validar que el número de documento no exista (excluyendo eliminados)
        if (clienteRepository.existsByNumeroDocumentoAndEstadoNot(cliente.getNumeroDocumento(), 2)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese número de documento");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Optional<Cliente> cambiarEstadoCliente(Long id) {
        return clienteRepository.findById(id).map(cliente -> {
            if (cliente.getEstado() == 1) {
                cliente.setEstado(0);
            } else if (cliente.getEstado() == 0) {
                cliente.setEstado(1);
            }
            return clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setEstado(2);
            clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional
    public Optional<Cliente> actualizarCliente(Long id, String tipoDocumento, String numeroDocumento, String nombreCompleto, String direccion) {
        return clienteRepository.findById(id).map(cliente -> {
            // Validar que el número de documento no esté duplicado
            if (clienteRepository.existsByNumeroDocumentoAndEstadoNot(numeroDocumento, 2) &&
                    !cliente.getNumeroDocumento().equals(numeroDocumento)) {
                throw new IllegalArgumentException("Ya existe un cliente con ese número de documento");
            }

            cliente.setTipoDocumento(tipoDocumento);
            cliente.setNumeroDocumento(numeroDocumento);
            cliente.setNombreCompleto(nombreCompleto);
            cliente.setDireccion(direccion);
            return clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumentoAndEstadoNot(numeroDocumento, 2);
    }
}