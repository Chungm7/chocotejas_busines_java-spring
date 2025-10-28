package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.dto.DniApiResponse;
import com.example.sistema_venta_chocotejas.dto.RucApiResponse;
import com.example.sistema_venta_chocotejas.service.ExternalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiServiceImpl.class);

    private final WebClient webClient;

    @Value("${api.miapicloud.token}")
    private String apiToken;

    @Value("${api.miapicloud.url}")
    private String apiUrl;

    public ExternalApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    @Override
    public Mono<RucApiResponse> consultarRUC(String ruc) {
        log.info("Consultando RUC: {}", ruc);
        return webClient.get()
                .uri("/ruc/{ruc}", ruc)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RucApiResponse.class)
                .doOnError(error -> log.error("Error al consultar RUC {}: {}", ruc, error.getMessage()))
                .onErrorResume(error -> {
                    RucApiResponse errorResponse = new RucApiResponse();
                    errorResponse.setSuccess(false);
                    // Aquí podrías agregar un mensaje de error más específico si lo deseas
                    return Mono.just(errorResponse);
                });
    }

    @Override
    public Mono<DniApiResponse> consultarDNI(String dni) {
        log.info("Consultando DNI: {}", dni);
        return webClient.get()
                .uri("/dni/{dni}", dni)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DniApiResponse.class)
                .doOnError(error -> log.error("Error al consultar DNI {}: {}", dni, error.getMessage()))
                .onErrorResume(error -> {
                    DniApiResponse errorResponse = new DniApiResponse();
                    errorResponse.setSuccess(false);
                    // Aquí podrías agregar un mensaje de error más específico si lo deseas
                    return Mono.just(errorResponse);
                });
    }
}
