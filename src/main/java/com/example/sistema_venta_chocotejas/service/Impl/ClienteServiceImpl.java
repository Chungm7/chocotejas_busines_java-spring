package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Cliente;
import com.example.sistema_venta_chocotejas.repository.ClienteRepository;
import com.example.sistema_venta_chocotejas.service.ClienteService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final RestTemplate restTemplate;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozNjAsImV4cCI6MTc2MDM4Mzk3OX0.s8pdevKFpdbjfpK8gz7bBmgh18GgEvIt8b_VUsjksKw";

    public ClienteServiceImpl(ClienteRepository clienteRepository, RestTemplate restTemplate) {
        this.clienteRepository = clienteRepository;
        this.restTemplate = restTemplate;
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
    public Optional<Cliente> actualizarCliente(Long id, Cliente clienteActualizado) {
        return clienteRepository.findById(id).map(cliente -> {
            // Validar que el número de documento no esté duplicado
            if (clienteRepository.existsByNumeroDocumentoAndEstadoNot(clienteActualizado.getNumeroDocumento(), 2) &&
                    !cliente.getNumeroDocumento().equals(clienteActualizado.getNumeroDocumento())) {
                throw new IllegalArgumentException("Ya existe un cliente con ese número de documento");
            }
            cliente.setTipoDocumento(clienteActualizado.getTipoDocumento());
            cliente.setNumeroDocumento(clienteActualizado.getNumeroDocumento());
            cliente.setNombreCompleto(clienteActualizado.getNombreCompleto());
            cliente.setDireccion(clienteActualizado.getDireccion());
            return clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumentoAndEstadoNot(numeroDocumento, 2);
    }

    @Override
    public Cliente validarYConstruirCliente(String tipoDocumento, String numeroDocumento, String direccion) throws IOException {
        // Validar el documento según el tipo
        if (tipoDocumento.equals("DNI")) {
            if (numeroDocumento.length() != 8) {
                throw new IllegalArgumentException("DNI debe tener 8 dígitos");
            }
            // Consultar API DNI
            String url = "https://miapi.cloud/v1/dni/" + numeroDocumento;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + TOKEN);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<DniApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, DniApiResponse.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IOException("Error al consultar el DNI");
            }

            DniApiResponse dniData = response.getBody();
            if (!dniData.isSuccess()) {
                throw new IOException("DNI no encontrado");
            }

            // Construir el nombre completo
            String nombreCompleto = dniData.getDatos().getNombres() + " " +
                    dniData.getDatos().getApePaterno() + " " +
                    dniData.getDatos().getApeMaterno();

            Cliente cliente = new Cliente();
            cliente.setTipoDocumento(tipoDocumento);
            cliente.setNumeroDocumento(numeroDocumento);
            cliente.setNombreCompleto(nombreCompleto);
            cliente.setDireccion(direccion);
            cliente.setEstado(1); // DNI siempre activo

            return cliente;

        } else if (tipoDocumento.equals("RUC")) {
            if (numeroDocumento.length() != 11) {
                throw new IllegalArgumentException("RUC debe tener 11 dígitos");
            }
            // Consultar API RUC
            String url = "https://miapi.cloud/v1/ruc/" + numeroDocumento;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + TOKEN);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<RucApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, RucApiResponse.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IOException("Error al consultar el RUC");
            }

            RucApiResponse rucData = response.getBody();
            if (!rucData.isSuccess()) {
                throw new IOException("RUC no encontrado");
            }

            // Validar que el RUC esté activo
            if (!rucData.getDatos().getEstado().equals("ACTIVO")) {
                throw new IOException("RUC no está activo");
            }

            Cliente cliente = new Cliente();
            cliente.setTipoDocumento(tipoDocumento);
            cliente.setNumeroDocumento(numeroDocumento);
            cliente.setNombreCompleto(rucData.getDatos().getRazonSocial());
            cliente.setDireccion(direccion);
            cliente.setEstado(1);

            return cliente;

        } else {
            throw new IllegalArgumentException("Tipo de documento no válido");
        }
    }

    // Clases internas para mapear la respuesta de las APIs
    public static class DniApiResponse {
        private boolean success;
        private DniDatos datos;
        // Getters y Setters
        public boolean isSuccess() {
            return success;
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public DniDatos getDatos() {
            return datos;
        }
        public void setDatos(DniDatos datos) {
            this.datos = datos;
        }
    }

    public static class DniDatos {
        private String dni;
        private String nombres;
        private String apePaterno;
        private String apeMaterno;
        // Getters y Setters
        public String getDni() {
            return dni;
        }
        public void setDni(String dni) {
            this.dni = dni;
        }
        public String getNombres() {
            return nombres;
        }
        public void setNombres(String nombres) {
            this.nombres = nombres;
        }
        public String getApePaterno() {
            return apePaterno;
        }
        public void setApePaterno(String apePaterno) {
            this.apePaterno = apePaterno;
        }
        public String getApeMaterno() {
            return apeMaterno;
        }
        public void setApeMaterno(String apeMaterno) {
            this.apeMaterno = apeMaterno;
        }
    }

    public static class RucApiResponse {
        private boolean success;
        private RucDatos datos;
        // Getters y Setters
        public boolean isSuccess() {
            return success;
        }
        public void setSuccess(boolean success) {
            this.success = success;
        }
        public RucDatos getDatos() {
            return datos;
        }
        public void setDatos(RucDatos datos) {
            this.datos = datos;
        }
    }

    public static class RucDatos {
        private String ruc;
        private String razonSocial;
        private String estado;
        // Getters y Setters
        public String getRuc() {
            return ruc;
        }
        public void setRuc(String ruc) {
            this.ruc = ruc;
        }
        public String getRazonSocial() {
            return razonSocial;
        }
        public void setRazonSocial(String razonSocial) {
            this.razonSocial = razonSocial;
        }
        public String getEstado() {
            return estado;
        }
        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}