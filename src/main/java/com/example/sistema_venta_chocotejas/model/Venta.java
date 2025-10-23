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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 50)
    private String tipoPago; // "Contado" o "Crédito"

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(nullable = false)
    private Double montoPagado = 0.0;

    @Column(nullable = false, length = 50)
    private String estadoPago; // "Pendiente" o "Pagado"


    @Column(nullable = false)
    private Integer estado = 1; // 1: activo, 0: eliminado

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos = new ArrayList<>();

    // Constructores
    public Venta() {
        this.fecha = LocalDateTime.now();
        this.total = 0.0;
        this.montoPagado = 0.0;
    }

    public Venta(Double total) {
        this();
        this.total = total != null ? total : 0.0;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public Double getTotal() { return total != null ? total : 0.0; }
    public void setTotal(Double total) { this.total = total != null ? total : 0.0; }

    public Double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(Double montoPagado) { this.montoPagado = montoPagado; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public List<DetalleVenta> getDetalleVentas() { return detalleVentas; }
    public void setDetalleVentas(List<DetalleVenta> detalleVentas) { this.detalleVentas = detalleVentas; }

    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            detalleVentas.add(detalle);
            detalle.setVenta(this);
        }
    }

    public void agregarPago(Pago pago) {
        if (pago != null) {
            pagos.add(pago);
            pago.setVenta(this);
        }
    }
}