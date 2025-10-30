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
    private Integer cantidad; // Positivo para incremento, negativo para decremento

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

    public MovimientoInventario(String tipoMovimiento, Integer cantidad, String comprobante, Producto producto, String observaciones) {
        this();
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

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}