package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "productos")
public class Producto {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(min = 10, max = 150, message = "La descripción debe tener entre 10 y 150 caracteres")
    @Column(length = 150)
    private String descripcion = "Sin descripción";

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    // CAMBIO: Quitar @Positive y usar @Min(0) para permitir stock 0
    @Column()
    private Integer stock = 0; // Valor por defecto 0

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "id_categoria")
    private Categoria categoria;

    @NotBlank(message = "La imagen es obligatoria")
    @Column(nullable = false)
    private String imagen;

    @Column(nullable = false)
    private Integer estado = 1;

    public Producto() {
    }

    // Getters y setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) {
        this.stock = stock != null ? stock : 0; // Asegurar que nunca sea null
    }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
                ", imagen='" + imagen + '\'' +
                ", estado=" + estado +
                '}';
    }
}