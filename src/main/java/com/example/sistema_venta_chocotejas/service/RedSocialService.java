package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.RedSocial;
import java.util.List;
import java.util.Optional;

public interface RedSocialService {
    List<RedSocial> listarRedesSocialesActivas();
    Optional<RedSocial> obtenerRedSocialPorId(Long id);
    RedSocial guardarRedSocial(RedSocial redSocial);
    Optional<RedSocial> cambiarEstadoRedSocial(Long id);
    void eliminarRedSocial(Long id);
    List<RedSocial> listarRedesSocialesActivasFrontend();
    Long contarRedesSocialesActivas();
    Long contarRedesSocialesInactivas();
    Optional<RedSocial> activarRedSocial(Long id);
}