package com.example.sistema_venta_chocotejas.model;

import com.example.sistema_venta_chocotejas.Enum.IconoRedSocial;
import jakarta.persistence.*;

@Entity
@Table(name = "redes_sociales")
public class RedSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "icono", nullable = false)
    private IconoRedSocial icono;

    @Column(name = "estado", nullable = false)
    private Integer estado = 1; // 1 = Activo, 0 = Inactivo 2= Eliminado

    // Constructores
    public RedSocial() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public IconoRedSocial getIcono() {
        return icono;
    }

    public void setIcono(IconoRedSocial icono) {
        this.icono = icono;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return "RedesSociales{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", url='" + url + '\'' +
                ", icono=" + icono +
                ", estado=" + estado +
                '}';
    }
}