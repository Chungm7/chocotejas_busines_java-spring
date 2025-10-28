package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.dto.DniApiResponse;
import com.example.sistema_venta_chocotejas.dto.RucApiResponse;
import reactor.core.publisher.Mono;

public interface ExternalApiService {
    Mono<RucApiResponse> consultarRUC(String ruc);
    Mono<DniApiResponse> consultarDNI(String dni);
}
