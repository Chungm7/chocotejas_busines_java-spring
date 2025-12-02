package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.dto.NodeServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class NodeIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(NodeIntegrationService.class);
    private final WebClient webClient;

    @Value("${node.service1.url:http://node-service-1:3001}")
    private String nodeService1Url;

    @Value("${node.service2.url:http://node-service-2:3002}")
    private String nodeService2Url;

    @Value("${node.service3.url:http://node-service-3:3003}")
    private String nodeService3Url;

    public NodeIntegrationService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene datos del primer servicio Node (por ejemplo: estadísticas de ventas)
     */
    public Mono<NodeServiceResponse> getService1Data() {
        return webClient.get()
                .uri(nodeService1Url + "/api/data")
                .retrieve()
                .bodyToMono(NodeServiceResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> logger.error("Error al consumir servicio Node 1: {}", error.getMessage()))
                .onErrorResume(error -> {
                    NodeServiceResponse errorResponse = new NodeServiceResponse();
                    errorResponse.setServiceName("Servicio 1");
                    errorResponse.setStatus("error");
                    errorResponse.setMessage("Error: " + error.getMessage());
                    errorResponse.setData(new HashMap<>());
                    return Mono.just(errorResponse);
                });
    }

    /**
     * Obtiene datos del segundo servicio Node (por ejemplo: análisis de productos)
     */
    public Mono<NodeServiceResponse> getService2Data() {
        return webClient.get()
                .uri(nodeService2Url + "/api/data")
                .retrieve()
                .bodyToMono(NodeServiceResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> logger.error("Error al consumir servicio Node 2: {}", error.getMessage()))
                .onErrorResume(error -> {
                    NodeServiceResponse errorResponse = new NodeServiceResponse();
                    errorResponse.setServiceName("Servicio 2");
                    errorResponse.setStatus("error");
                    errorResponse.setMessage("Error: " + error.getMessage());
                    errorResponse.setData(new HashMap<>());
                    return Mono.just(errorResponse);
                });
    }

    /**
     * Obtiene datos del tercer servicio Node (por ejemplo: reportes de clientes)
     */
    public Mono<NodeServiceResponse> getService3Data() {
        return webClient.get()
                .uri(nodeService3Url + "/api/data")
                .retrieve()
                .bodyToMono(NodeServiceResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> logger.error("Error al consumir servicio Node 3: {}", error.getMessage()))
                .onErrorResume(error -> {
                    NodeServiceResponse errorResponse = new NodeServiceResponse();
                    errorResponse.setServiceName("Servicio 3");
                    errorResponse.setStatus("error");
                    errorResponse.setMessage("Error: " + error.getMessage());
                    errorResponse.setData(new HashMap<>());
                    return Mono.just(errorResponse);
                });
    }

    /**
     * Obtiene datos de los 3 servicios Node de forma concurrente
     */
    public Map<String, NodeServiceResponse> getAllServicesData() {
        Map<String, NodeServiceResponse> responses = new HashMap<>();

        Mono.zip(getService1Data(), getService2Data(), getService3Data())
                .map(tuple -> {
                    responses.put("service1", tuple.getT1());
                    responses.put("service2", tuple.getT2());
                    responses.put("service3", tuple.getT3());
                    return responses;
                })
                .block(); // Bloquea para esperar todas las respuestas (necesario para Thymeleaf)

        return responses;
    }
}

