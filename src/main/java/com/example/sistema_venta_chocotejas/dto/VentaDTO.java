package com.example.sistema_venta_chocotejas.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    private ClienteDTO cliente;
    private List<DetalleVentaDTO> detalleVentas;
    private Integer estado;

    // NUEVOS CAMPOS
    private String comprobantePago;
    private String codigoVenta;

    // Constructores
    public VentaDTO() {}

    public VentaDTO(Long id, LocalDateTime fecha, Double total, ClienteDTO cliente,
                    List<DetalleVentaDTO> detalleVentas, Integer estado,
                    String comprobantePago, String codigoVenta) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.cliente = cliente;
        this.detalleVentas = detalleVentas;
        this.estado = estado;
        this.comprobantePago = comprobantePago;
        this.codigoVenta = codigoVenta;
    }

    // Getters y Setters
    public String getComprobantePago() { return comprobantePago; }
    public void setComprobantePago(String comprobantePago) { this.comprobantePago = comprobantePago; }

    public String getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(String codigoVenta) { this.codigoVenta = codigoVenta; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

    public List<DetalleVentaDTO> getDetalleVentas() { return detalleVentas; }
    public void setDetalleVentas(List<DetalleVentaDTO> detalleVentas) { this.detalleVentas = detalleVentas; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}