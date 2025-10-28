package com.example.sistema_venta_chocotejas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DniApiResponse {
    private boolean success;
    private DniData datos;

    @Data
    public static class DniData {
        private String dni;
        private String nombres;
        @JsonProperty("ape_paterno")
        private String apePaterno;
        @JsonProperty("ape_materno")
        private String apeMaterno;
        private Domiciliado domiciliado;
    }

    @Data
    public static class Domiciliado {
        @JsonProperty("dirección")
        private String direccion;
        private String distrito;
        private String provincia;
        private String departamento;
        private String ubigeo;
    }
}
