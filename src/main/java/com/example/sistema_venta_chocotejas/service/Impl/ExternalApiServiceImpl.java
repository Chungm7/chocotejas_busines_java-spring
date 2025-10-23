package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.service.ExternalApiService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestTemplate restTemplate;

    @Value("${api.miapicloud.token}")
    private String apiToken;

    public ExternalApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public JsonNode consultarRUC(String ruc) {
        String url = "https://api.miapicloud.com/ruc/" + ruc + "?token=" + apiToken;
        return restTemplate.getForObject(url, JsonNode.class);
    }

    @Override
    public JsonNode consultarDNI(String dni) {
        String url = "https://api.miapicloud.com/dni/" + dni + "?token=" + apiToken;
        return restTemplate.getForObject(url, JsonNode.class);
    }
}
