package com.example.sistema_venta_chocotejas.dto;

import java.util.Map;

public class NodeServiceResponse {
    private String serviceName;
    private String status;
    private Map<String, Object> data;
    private String message;

    public NodeServiceResponse() {
    }

    public NodeServiceResponse(String serviceName, String status, Map<String, Object> data, String message) {
        this.serviceName = serviceName;
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

