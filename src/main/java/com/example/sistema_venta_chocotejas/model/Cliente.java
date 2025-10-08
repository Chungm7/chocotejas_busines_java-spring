package com.example.sistema_venta_chocotejas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Column(nullable = false, length = 3)
    private String tipoDocumento; // "DNI" o "RUC"

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 11, message = "El número de documento debe tener entre 8 y 11 dígitos")
    @Column(nullable = false, unique = true, length = 11)
    private String numeroDocumento;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre completo debe tener entre 3 y 255 caracteres")
    @Column(nullable = false, length = 255)
    private String nombreCompleto;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 255, message = "La dirección debe tener entre 5 y 255 caracteres")
    @Column(nullable = false, length = 255)
    private String direccion;

    @NotNull
    @Column(nullable = false)
    private Integer estado = 1; // 1: Activo, 0: Inactivo, 2: Eliminado

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento != null ? tipoDocumento.trim() : null;
    }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento != null ? numeroDocumento.trim() : null;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto != null ? nombreCompleto.trim() : null;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        this.direccion = direccion != null ? direccion.trim() : null;
    }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}