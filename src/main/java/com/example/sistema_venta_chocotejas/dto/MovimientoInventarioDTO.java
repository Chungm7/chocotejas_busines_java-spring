// MovimientoInventarioDTO.java
package com.example.sistema_venta_chocotejas.dto;

import java.time.LocalDateTime;

public class MovimientoInventarioDTO {
    private Long id;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private Integer stockAnterior; // NUEVO
    private Integer nuevoStock; // NUEVO
    private Integer diferencia;
    private String comprobante;
    private ProductoDTO producto;
    private String observaciones;

    // Constructores
    public MovimientoInventarioDTO() {}

    public MovimientoInventarioDTO(Long id, LocalDateTime fecha, String tipoMovimiento,
                                   Integer stockAnterior, Integer nuevoStock, Integer diferencia,
                                   String comprobante, ProductoDTO producto, String observaciones) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.stockAnterior = stockAnterior;
        this.nuevoStock = nuevoStock;
        this.diferencia = diferencia;
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

    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }

    public Integer getNuevoStock() { return nuevoStock; }
    public void setNuevoStock(Integer nuevoStock) { this.nuevoStock = nuevoStock; }

    public Integer getDiferencia() { return diferencia; }
    public void setDiferencia(Integer diferencia) { this.diferencia = diferencia; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }

    public ProductoDTO getProducto() { return producto; }
    public void setProducto(ProductoDTO producto) { this.producto = producto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}