package com.example.sistema_venta_chocotejas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RucApiResponse {
    private boolean success;
    private RucData datos;

    @Data
    public static class RucData {
        private String ruc;
        @JsonProperty("razon_social")
        private String razonSocial;
        private String estado;
        private String condicion;
        private Domiciliado domiciliado;
    }

    @Data
    public static class Domiciliado {
        private String direccion;
        private String distrito;
        private String provincia;
        private String departamento;
        private String ubigeo;
    }
}
