package com.example.sistema_venta_chocotejas.dto;

import java.time.LocalDateTime;

public class MovimientoInventarioDTO {
    private Long id;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private Integer cantidad;
    private String comprobante;
    private ProductoDTO producto;
    private String observaciones;

    // Constructores
    public MovimientoInventarioDTO() {}

    public MovimientoInventarioDTO(Long id, LocalDateTime fecha, String tipoMovimiento,
                                   Integer cantidad, String comprobante, ProductoDTO producto,
                                   String observaciones) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.comprobante = comprobante;
        this.producto = producto;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }

    public ProductoDTO getProducto() { return producto; }
    public void setProducto(ProductoDTO producto) { this.producto = producto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}