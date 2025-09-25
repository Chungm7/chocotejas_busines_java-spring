package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "productos")
public class Producto {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(min = 10, max = 150, message = "La descripción debe tener entre 10 y 150 caracteres")
    @Column(length = 150)
    @ColumnDefault("'Sin descripción'")
    private String descripcion;

    @NotBlank(message = "El precio es obligatorio")
    @Column(nullable = false)
    private double precio;

    @Column()
    @ColumnDefault("0")
    private int stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "id_categoria")
    private Categoria categoria;

    @NotBlank(message = "La imagen es obligatoria")
    @Column(nullable = false)
    private String imagen;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer estado; // 1: Activo, 0: Inactivo, 2: Eliminado

    public Producto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoria=" + categoria +
                ", imagen='" + imagen + '\'' +
                ", estado=" + estado +
                '}';
    }
}
