package com.example.sistema_venta_chocotejas.controller.cliente;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.service.CategoriaService;
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

    public GaleriaController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        // Obtener productos activos (estado = 1) y categorías activas
        List<Producto> productosActivos = productoService.listarProductosActivos1();
        List<Categoria> categoriasActivas = categoriaService.listarCategorias1();

        model.addAttribute("productos", productosActivos);
        model.addAttribute("categorias", categoriasActivas);
        return "client/galeria";
    }
}