package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Pago;

public interface PagoService {
    Pago registrarPago(Long ventaId, Double monto);
}
