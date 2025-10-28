package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.dto.DniApiResponse;
import com.example.sistema_venta_chocotejas.dto.RucApiResponse;
import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.repository.ClienteRepository;
import com.example.sistema_venta_chocotejas.service.ClienteService;
import com.example.sistema_venta_chocotejas.service.ExternalApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

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
    public Mono<Cliente> buscarOCrearCliente(String tipoDocumento, String numeroDocumento) {
        return Mono.justOrEmpty(buscarPorDocumento(numeroDocumento))
                .switchIfEmpty(Mono.defer(() -> {
                    if ("DNI".equalsIgnoreCase(tipoDocumento)) {
                        return externalApiService.consultarDNI(numeroDocumento)
                                .flatMap(this::crearClienteDesdeDni);
                    } else if ("RUC".equalsIgnoreCase(tipoDocumento)) {
                        return externalApiService.consultarRUC(numeroDocumento)
                                .flatMap(this::crearClienteDesdeRuc);
                    } else {
                        return Mono.error(new IllegalArgumentException("Tipo de documento no válido: " + tipoDocumento));
                    }
                }));
    }

    private Mono<Cliente> crearClienteDesdeDni(DniApiResponse apiResponse) {
        if (!apiResponse.isSuccess() || apiResponse.getDatos() == null) {
            return Mono.error(new RuntimeException("No se pudo obtener datos del DNI."));
        }
        DniApiResponse.DniData data = apiResponse.getDatos();
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setTipoDocumento("DNI");
        nuevoCliente.setNumeroDocumento(data.getDni());
        nuevoCliente.setNombreCompleto(data.getNombres() + " " + data.getApePaterno() + " " + data.getApeMaterno());
        nuevoCliente.setDireccion(""); // Dirección vacía como se solicitó
        nuevoCliente.setEstado(1); // Activo
        return Mono.just(guardarCliente(nuevoCliente));
    }

    private Mono<Cliente> crearClienteDesdeRuc(RucApiResponse apiResponse) {
        if (!apiResponse.isSuccess() || apiResponse.getDatos() == null) {
            return Mono.error(new RuntimeException("No se pudo obtener datos del RUC."));
        }
        RucApiResponse.RucData data = apiResponse.getDatos();
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setTipoDocumento("RUC");
        nuevoCliente.setNumeroDocumento(data.getRuc());
        nuevoCliente.setNombreCompleto(data.getRazonSocial());
        nuevoCliente.setDireccion(""); // Dirección vacía como se solicitó
        nuevoCliente.setEstado(1); // Activo
        return Mono.just(guardarCliente(nuevoCliente));
    }
}
