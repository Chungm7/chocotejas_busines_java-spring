package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.service.ExternalApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.miapicloud.token}")
    private String apiToken;

    public ExternalApiServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode consultarRUC(String ruc) {
        String url = "https://miapi.cloud/v1/ruc/" + ruc;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", "Error al consultar RUC: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return errorNode;
        }
    }

    @Override
    public JsonNode consultarDNI(String dni) {
        String url = "https://miapi.cloud/v1/dni/" + dni;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", "Error al consultar DNI: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return errorNode;
        }
    }
}
