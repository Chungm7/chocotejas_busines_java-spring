package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Pago;
import com.example.sistema_venta_chocotejas.model.Venta;
import com.example.sistema_venta_chocotejas.repository.PagoRepository;
import com.example.sistema_venta_chocotejas.repository.VentaRepository;
import com.example.sistema_venta_chocotejas.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;

    public PagoServiceImpl(PagoRepository pagoRepository, VentaRepository ventaRepository) {
        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional
    public Pago registrarPago(Long ventaId, Double monto) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + ventaId));

        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser positivo");
        }

        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(monto);
        pagoRepository.save(pago);

        venta.setMontoPagado(venta.getMontoPagado() + monto);
        if (venta.getMontoPagado() >= venta.getTotal()) {
            venta.setEstadoPago("Pagado");
        }
        ventaRepository.save(venta);

        return pago;
    }
}
