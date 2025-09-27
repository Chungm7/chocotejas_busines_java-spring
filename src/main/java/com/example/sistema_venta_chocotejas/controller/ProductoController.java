package com.example.sistema_venta_chocotejas.controller;

import com.example.sistema_venta_chocotejas.model.Producto;
import com.example.sistema_venta_chocotejas.service.Impl.CategoriaIServicempl;
import com.example.sistema_venta_chocotejas.service.Impl.ProductoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoServiceImpl productoService;
    private final CategoriaIServicempl categoriaService;

    public ProductoController(ProductoServiceImpl productoService, CategoriaIServicempl categoriaService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaProductos() {
        return "gestion-productos"; // Devuelve el nombre de la vista (productos.html)
    }

    @GetMapping("api/listar")
    @ResponseBody
    public ResponseEntity<?> listarProductos() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", productoService.listarProductosActivos());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(producto -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    // Preparamos los datos para el frontend
                    Map<String, Object> productoData = new HashMap<>();
                    productoData.put("id", producto.getId());
                    productoData.put("nombre", producto.getNombre());
                    productoData.put("descripcion", producto.getDescripcion());
                    productoData.put("precio", producto.getPrecio());
                    productoData.put("estado", producto.getEstado());
                    productoData.put("categoria", producto.getCategoria().getNombre());

                    response.put("data", productoData);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<Producto> guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoria") Long categoriaId,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

        try {
            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre(nombre);
            nuevoProducto.setDescripcion(descripcion);
            nuevoProducto.setPrecio(precio);
            nuevoProducto.setStock(stock);
            // Aquí deberías establecer la categoría usando el ID proporcionado
            // Por simplicidad, este ejemplo no incluye esa lógica
            Producto productoGuardado = productoService.guardarProducto(nuevoProducto, imagenFile);
            return new ResponseEntity<>(productoGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precio", required = false) Double precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            @RequestParam(value = "categoria", required = false) Long categoriaId,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

        try {
            Producto producto = productoService.obtenerProductoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Actualizar solo los campos que no sean nulos o vacíos
            if (nombre != null && !nombre.isBlank()) {
                producto.setNombre(nombre);
            }
            if (descripcion != null && !descripcion.isBlank()) {
                producto.setDescripcion(descripcion);
            }
            if (precio != null) {
                producto.setPrecio(precio);
            }
            if (stock != null) {
                producto.setStock(stock);
            }
            if (categoriaId != null) {
                producto.setCategoria(
                        categoriaService.obtenerCategoriaPorId(categoriaId)
                                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"))
                );
            }

            Producto productoActualizado = productoService.guardarProducto(producto, imagenFile);
            return ResponseEntity.ok(productoActualizado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return productoService.cambiarEstadoProducto(id)
                    .map(producto -> {
                        response.put("success", true);
                        response.put("message", producto.getEstado() == 1 ? "Producto habilitado con éxito" : "Producto deshabilitado con éxito");
                        response.put("data", producto);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        response.put("success", false);
                        response.put("message", "Producto no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/listar-productos-categoria")
    @ResponseBody
    public ResponseEntity<?> listarProductosCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", productoService.listarProductosporCategoria(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/actualizar-stock/{idProducto}")
    @ResponseBody
    public ResponseEntity<?> actualizarStockProducto(@PathVariable Long idProducto, @RequestParam int cantidad) {
        Map<String, Object> response = new HashMap<>();
        try {
            Producto productoActualizado = productoService.actualizarStockProducto(idProducto, cantidad);
            response.put("success", true);
            response.put("message", "Stock actualizado con éxito");
            response.put("data", productoActualizado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el stock del producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (productoService.obtenerProductoPorId(id).isEmpty()) {
                response.put("success", true);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
                productoService.eliminarProducto(id);
                response.put("success", true);
                response.put("message", "Producto eliminado con éxito");
                return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Producto no encontrado");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

