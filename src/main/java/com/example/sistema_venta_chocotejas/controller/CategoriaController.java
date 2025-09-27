package com.example.sistema_venta_chocotejas.controller;

import com.example.sistema_venta_chocotejas.model.Categoria;
import com.example.sistema_venta_chocotejas.service.Impl.CategoriaIServicempl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaIServicempl categoriaService;

    public CategoriaController(CategoriaIServicempl categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaCategorias() {
        return "gestion-categorias"; // Devuelve el nombre de la vista (categorias.html)
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarCategoriasApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categoriaService.listarCategoriasActivas());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        return categoriaService.obtenerCategoriaPorId(id)
                .map(categoria -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    // Preparamos los datos para el frontend
                    Map<String, Object> categoriaData = new HashMap<>();
                    categoriaData.put("id", categoria.getId());
                    categoriaData.put("nombre", categoria.getNombre());
                    categoriaData.put("descripcion", categoria.getDescripcion());
                    categoriaData.put("estado", categoria.getEstado());

                    response.put("data", categoriaData);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCategoria(@RequestBody Categoria categoria) {
        Map<String, Object> response = new HashMap<>();
        try {
            Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);
            response.put("success", true);
            response.put("message", categoria.getNombre() != null ? "Categoría actualizada con éxito" : "Categoría creada con éxito");
            response.put("data", categoriaGuardada);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar la categoría: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return categoriaService.cambiarEstadoCategoria(id)
                    .map(categoria -> {
                        response.put("success", true);
                        response.put("message", categoria.getEstado() == 1 ? "Categoría activada" : "Categoría desactivada");
                        response.put("data", categoria);
                        return ResponseEntity.ok(response);
                    }).orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Categoría no encontrada");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado de la categoría: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/listar-categorias-productos")
    @ResponseBody
    public ResponseEntity<?> listarCategoriasConProductos() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categoriaService.listarProductosporCategoria());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

           try {
            // Buena práctica: verificar si el usuario existe antes de intentar eliminarlo.
            if (categoriaService.obtenerCategoriaPorId(id).isEmpty()) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            categoriaService.eliminarCategoria(id);
            response.put("success", true);
            response.put("message", "Usuario eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar usuario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
