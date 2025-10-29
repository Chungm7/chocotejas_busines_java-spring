package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(nullable = false)
    private Integer estado = 1; // 1: activo, 0: eliminado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente_id;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    // Constructores
    public Venta() {
        this.fecha = LocalDateTime.now();
        this.total = 0.0;
    }

    public Venta(Double total) {
        this();
        this.total = total != null ? total : 0.0;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Double getTotal() { return total != null ? total : 0.0; }
    public void setTotal(Double total) { this.total = total != null ? total : 0.0; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente_id; }
    public void setCliente(Cliente cliente) { this.cliente_id = cliente; }

    public List<DetalleVenta> getDetalleVentas() { return detalleVentas; }
    public void setDetalleVentas(List<DetalleVenta> detalleVentas) { this.detalleVentas = detalleVentas; }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            detalleVentas.add(detalle);
            detalle.setVenta(this);
        }
    }
}