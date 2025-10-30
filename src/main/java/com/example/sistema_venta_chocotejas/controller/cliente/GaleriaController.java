package com.example.sistema_venta_chocotejas.controller.cliente;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Contacto;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.model.RedSocial;
import com.example.sistema_venta_chocotejas.service.CategoriaService;
import com.example.sistema_venta_chocotejas.service.Impl.ContactoServiceImpl;
import com.example.sistema_venta_chocotejas.service.Impl.RedSocialServiceImpl;
import com.example.sistema_venta_chocotejas.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller()
@RequestMapping("/galeria")
public class GaleriaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ContactoServiceImpl contactoService;
    private final RedSocialServiceImpl redSocialService;

    public GaleriaController(ProductoService productoService,
                             CategoriaService categoriaService,
                             ContactoServiceImpl contactoService,
                             RedSocialServiceImpl redSocialService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.contactoService = contactoService;
        this.redSocialService = redSocialService;
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        // Obtener productos activos (estado = 1) y categorías activas
        List<Producto> productosActivos = productoService.listarProductosActivos1();
        List<Categoria> categoriasActivas = categoriaService.listarCategorias1();
        Contacto contacto = contactoService.obtenerContacto().orElse(null);
        List<RedSocial> redSocials= redSocialService.listarRedesSocialesActivas();

        model.addAttribute("productos", productosActivos);
        model.addAttribute("categorias", categoriasActivas);
        model.addAttribute("contacto", contacto);
        model.addAttribute("redSocials", redSocials);
        return "client/galeria";
    }
}