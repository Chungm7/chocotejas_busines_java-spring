// MovimientoInventario.java
package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 20)
    private String tipoMovimiento; // "VENTA", "ACTUALIZACION_STOCK"

    @Column(nullable = false)
    private Integer stockAnterior; // NUEVO: Stock antes del movimiento

    @Column(nullable = false)
    private Integer nuevoStock; // NUEVO: Stock después del movimiento

    @Column(nullable = false)
    private Integer diferencia; // Positivo para incremento, negativo para decremento

    @Column(nullable = false, length = 50)
    private String comprobante; // Código de venta o "ACTUALIZACION_STOCK"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(length = 255)
    private String observaciones;

    // Constructores
    public MovimientoInventario() {
        this.fecha = LocalDateTime.now();
    }

    public MovimientoInventario(String tipoMovimiento, Integer stockAnterior,
                                Integer nuevoStock, String comprobante,
                                Producto producto, String observaciones) {
        this();
        this.tipoMovimiento = tipoMovimiento;
        this.stockAnterior = stockAnterior;
        this.nuevoStock = nuevoStock;
        this.diferencia = nuevoStock - stockAnterior;
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
    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
        calcularDiferencia();
    }

    public Integer getNuevoStock() { return nuevoStock; }
    public void setNuevoStock(Integer nuevoStock) {
        this.nuevoStock = nuevoStock;
        calcularDiferencia();
    }

    public Integer getDiferencia() { return diferencia; }
    public void setDiferencia(Integer diferencia) { this.diferencia = diferencia; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Método para calcular diferencia automáticamente
    private void calcularDiferencia() {
        if (this.stockAnterior != null && this.nuevoStock != null) {
            this.diferencia = this.nuevoStock - this.stockAnterior;
        }
    }
}