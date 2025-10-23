package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double subtotal;

    // Constructor por defecto
    public DetalleVenta() {
        // Inicializar con valores por defecto para evitar null
        this.cantidad = 0;
        this.precioUnitario = 0.0;
        this.subtotal = 0.0;
    }

    // Constructor con parámetros
    public DetalleVenta(Producto producto, Integer cantidad) {
        this();
        this.producto = producto;
        this.cantidad = cantidad != null ? cantidad : 0;

        // Validar y asignar precio
        if (producto != null && producto.getPrecio() != null) {
            this.precioUnitario = producto.getPrecio();
        } else {
            this.precioUnitario = 0.0;
        }

        // Calcular subtotal
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        // Actualizar precio unitario si el producto tiene precio
        if (producto != null && producto.getPrecio() != null) {
            this.precioUnitario = producto.getPrecio();
        }
        calcularSubtotal();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad != null ? cantidad : 0;
        calcularSubtotal();
    }

    public Double getPrecioUnitario() {
        return precioUnitario != null ? precioUnitario : 0.0;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario != null ? precioUnitario : 0.0;
        calcularSubtotal();
    }

    public Double getSubtotal() {
        return subtotal != null ? subtotal : 0.0;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal != null ? subtotal : 0.0;
    }

    // Método para calcular subtotal automáticamente
    private void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            this.subtotal = this.cantidad * this.precioUnitario;
        } else {
            this.subtotal = 0.0;
        }
    }
}