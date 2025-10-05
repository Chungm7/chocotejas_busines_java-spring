package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "redes_sociales")
public class RedesSociales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "icono", nullable = false)
    private RedSocial icono;

    @Column(name = "estado", nullable = false)
    private Integer estado = 1; // 1 = Activo, 0 = Inactivo 2= Eliminado

    // Constructores
    public RedesSociales() {
    }

    public RedesSociales(String nombre, String url, RedSocial icono, Integer estado) {
        this.nombre = nombre;
        this.url = url;
        this.icono = icono;
        this.estado = estado;
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

    public RedSocial getIcono() {
        return icono;
    }

    public void setIcono(RedSocial icono) {
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