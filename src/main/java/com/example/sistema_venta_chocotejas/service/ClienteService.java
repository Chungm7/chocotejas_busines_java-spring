package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Cliente;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listarClientesActivos();
    List<Cliente> listarTodosLosClientes();
    Optional<Cliente> obtenerClientePorId(Long id);
    Cliente guardarCliente(Cliente cliente);
    Optional<Cliente> cambiarEstadoCliente(Long id);
    void eliminarCliente(Long id);
    Optional<Cliente> actualizarCliente(Long id, String tipoDocumento, String numeroDocumento, String nombreCompleto, String direccion);
    Optional<Cliente> buscarPorDocumento(String numeroDocumento);
}