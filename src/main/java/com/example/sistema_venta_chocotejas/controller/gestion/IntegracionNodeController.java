package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.dto.NodeServiceResponse;
import com.example.sistema_venta_chocotejas.service.NodeIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/gestion/integracion")
public class IntegracionNodeController {

    private static final Logger logger = LoggerFactory.getLogger(IntegracionNodeController.class);
    private final NodeIntegrationService nodeIntegrationService;

    public IntegracionNodeController(NodeIntegrationService nodeIntegrationService) {
        this.nodeIntegrationService = nodeIntegrationService;
    }

    /**
     * Muestra la página de integración con los 3 servicios Node
     */
    @GetMapping
    public String mostrarIntegracion(Model model) {
        try {
            logger.info("Obteniendo datos de los servicios Node...");
            Map<String, NodeServiceResponse> serviciosData = nodeIntegrationService.getAllServicesData();

            model.addAttribute("service1", serviciosData.get("service1"));
            model.addAttribute("service2", serviciosData.get("service2"));
            model.addAttribute("service3", serviciosData.get("service3"));

            logger.info("Datos obtenidos exitosamente de los servicios Node");
        } catch (Exception e) {
            logger.error("Error al obtener datos de servicios Node: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar datos de servicios externos");
        }

        return "gestion/gestion-integracion-node";
    }

    /**
     * Endpoint API REST para obtener datos del servicio 1
     */
    @GetMapping("/api/service1")
    @ResponseBody
    public NodeServiceResponse getService1Data() {
        return nodeIntegrationService.getService1Data().block();
    }

    /**
     * Endpoint API REST para obtener datos del servicio 2
     */
    @GetMapping("/api/service2")
    @ResponseBody
    public NodeServiceResponse getService2Data() {
        return nodeIntegrationService.getService2Data().block();
    }

    /**
     * Endpoint API REST para obtener datos del servicio 3
     */
    @GetMapping("/api/service3")
    @ResponseBody
    public NodeServiceResponse getService3Data() {
        return nodeIntegrationService.getService3Data().block();
    }

    /**
     * Endpoint API REST para obtener datos de todos los servicios
     */
    @GetMapping("/api/all")
    @ResponseBody
    public Map<String, NodeServiceResponse> getAllServicesData() {
        return nodeIntegrationService.getAllServicesData();
    }
}

