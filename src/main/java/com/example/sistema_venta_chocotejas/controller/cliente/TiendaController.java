package com.example.sistema_venta_chocotejas.controller.cliente;

import com.example.sistema_venta_chocotejas.model.Contacto;
import com.example.sistema_venta_chocotejas.model.Inicio;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.service.Impl.ContactoServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.InicioServiceImpl;
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

    public TiendaController(ProductoService productoService,
                            InicioServiceImpl inicioService,
                            ContactoServiceImpl contactoService) {
        this.productoService = productoService;
        this.inicioService = inicioService;
        this.contactoService = contactoService;
    }

    @GetMapping("/inicio")
    public String home(Model model) {
        List<Producto> productosDestacados = productoService.listarProductosDestacadosActivos();
        Inicio inicio =  inicioService.obtenerInicio().orElse(null);
        Contacto contacto = contactoService.obtenerContacto().orElse(null);
        // Verificar si hay más de 4 productos para mostrar controles de navegación
        boolean mostrarControles = productosDestacados.size() > 4;

        model.addAttribute("inicio", inicio);
        model.addAttribute("productosDestacados", productosDestacados);
        model.addAttribute("mostrarControles", mostrarControles);
        model.addAttribute("contacto", contacto);

        return "client/indexclient";
    }
}