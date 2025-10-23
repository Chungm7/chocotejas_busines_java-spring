package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.repository.ClienteRepository;
import com.example.sistema_venta_chocotejas.service.ClienteService;
import com.example.sistema_venta_chocotejas.service.ExternalApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ExternalApiService externalApiService;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ExternalApiService externalApiService) {
        this.clienteRepository = clienteRepository;
        this.externalApiService = externalApiService;
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
            throw new IllegalArgumentException("Ya existe un cliente con ese número de documento: " + cliente.getNumeroDocumento());
        }

        // Validaciones adicionales
        if (cliente.getTipoDocumento() == null || cliente.getTipoDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio");
        }
        if (cliente.getNumeroDocumento() == null || cliente.getNumeroDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        if (cliente.getNombreCompleto() == null || cliente.getNombreCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        if (cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Optional<Cliente> cambiarEstadoCliente(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }

        Cliente cliente = obtenerClientePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        cliente.setEstado(2);
        clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Optional<Cliente> actualizarCliente(Long id, String tipoDocumento, String numeroDocumento, String nombreCompleto, String direccion) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }

        return clienteRepository.findById(id).map(cliente -> {
            boolean hasChanges = false;

            // Validar que el número de documento no esté duplicado (excluyendo el actual cliente y los eliminados)
            if (numeroDocumento != null && !numeroDocumento.trim().equals(cliente.getNumeroDocumento())) {
                if (clienteRepository.existsByNumeroDocumentoAndEstadoNotAndIdNot(numeroDocumento.trim(), 2, id)) {
                    throw new IllegalArgumentException("Ya existe un cliente con ese número de documento: " + numeroDocumento);
                }
                cliente.setNumeroDocumento(numeroDocumento.trim());
                hasChanges = true;
            }

            if (tipoDocumento != null && !tipoDocumento.trim().equals(cliente.getTipoDocumento())) {
                cliente.setTipoDocumento(tipoDocumento.trim());
                hasChanges = true;
            }

            if (nombreCompleto != null && !nombreCompleto.trim().equals(cliente.getNombreCompleto())) {
                cliente.setNombreCompleto(nombreCompleto.trim());
                hasChanges = true;
            }

            if (direccion != null && !direccion.trim().equals(cliente.getDireccion())) {
                cliente.setDireccion(direccion.trim());
                hasChanges = true;
            }

            if (!hasChanges) {
                throw new IllegalArgumentException("No se realizaron cambios en el cliente");
            }

            return clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumentoAndEstadoNot(numeroDocumento, 2);
    }

    @Override
    @Transactional
    public Cliente buscarOCrearCliente(String tipoDocumento, String numeroDocumento) {
        // Buscar cliente existente
        Optional<Cliente> clienteOpt = buscarPorDocumento(numeroDocumento);
        if (clienteOpt.isPresent()) {
            return clienteOpt.get();
        }

        // Si no existe, consultar la API externa
        JsonNode apiData;
        if ("DNI".equalsIgnoreCase(tipoDocumento)) {
            apiData = externalApiService.consultarDNI(numeroDocumento);
        } else if ("RUC".equalsIgnoreCase(tipoDocumento)) {
            apiData = externalApiService.consultarRUC(numeroDocumento);
        } else {
            throw new IllegalArgumentException("Tipo de documento no válido: " + tipoDocumento);
        }

        if (apiData == null || apiData.get("success").asBoolean() == false) {
            throw new RuntimeException("No se pudo obtener datos del documento: " + numeroDocumento);
        }

        // Crear y guardar el nuevo cliente
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setTipoDocumento(tipoDocumento);
        nuevoCliente.setNumeroDocumento(numeroDocumento);

        if ("DNI".equalsIgnoreCase(tipoDocumento)) {
            String nombres = apiData.get("datos").get("nombres").asText();
            String apePaterno = apiData.get("datos").get("ape_paterno").asText();
            String apeMaterno = apiData.get("datos").get("ape_materno").asText();
            nuevoCliente.setNombreCompleto(nombres + " " + apePaterno + " " + apeMaterno);
            nuevoCliente.setDireccion("");
        } else { // RUC
            nuevoCliente.setNombreCompleto(apiData.get("datos").get("razon_social").asText());
            nuevoCliente.setDireccion("");
        }

        nuevoCliente.setEstado(1); // Activo
        return guardarCliente(nuevoCliente);
    }
}