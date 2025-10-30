package com.example.sistema_venta_chocotejas.controller.cliente;

import com.example.sistema_venta_chocotejas.model.*;
import com.example.sistema_venta_chocotejas.service.Impl.ContactoServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.InicioServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.MomentoServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.RedSocialServiceImpl;
import com.example.sistema_venta_chocotejas.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    private final ProductoService productoService;
    private final InicioServiceImpl inicioService;
    private final ContactoServiceImpl contactoService;
    private final RedSocialServiceImpl redSocialService;
    private final MomentoServiceImpl momentoService;
    public TiendaController(ProductoService productoService,
                            InicioServiceImpl inicioService,
                            ContactoServiceImpl contactoService,
                            RedSocialServiceImpl redSocialService,
                            MomentoServiceImpl momentoService) {
        this.productoService = productoService;
        this.inicioService = inicioService;
        this.contactoService = contactoService;
        this.redSocialService = redSocialService;
        this.momentoService = momentoService;
    }

    @GetMapping("/inicio")
    public String home(Model model) {
        List<Producto> productosDestacados = productoService.listarProductosDestacadosActivos();
        Inicio inicio =  inicioService.obtenerInicio().orElse(null);
        Contacto contacto = contactoService.obtenerContacto().orElse(null);
        List<RedSocial> redSocials= redSocialService.listarRedesSocialesActivas();
        List<Momento> momentosActivos = momentoService.momentoActivo();
        // Verificar si hay más de 4 productos para mostrar controles de navegación
        boolean mostrarControles = productosDestacados.size() > 4;

        model.addAttribute("inicio", inicio);
        model.addAttribute("productosDestacados", productosDestacados);
        model.addAttribute("mostrarControles", mostrarControles);
        model.addAttribute("contacto", contacto);
        model.addAttribute("redSocials", redSocials);
        model.addAttribute("momentos", momentosActivos);
        return "client/indexclient";
    }
}