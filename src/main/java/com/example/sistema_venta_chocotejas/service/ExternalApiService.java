package com.example.sistema_venta_chocotejas.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ExternalApiService {
    JsonNode consultarRUC(String ruc);
    JsonNode consultarDNI(String dni);
}
