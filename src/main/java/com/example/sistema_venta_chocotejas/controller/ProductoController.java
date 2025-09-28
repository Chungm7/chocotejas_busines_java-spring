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
    public ResponseEntity<?> guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            @RequestParam("categoria") Long categoriaId,
            @RequestParam("imagenFile") MultipartFile imagenFile) {

        try {
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El nombre es obligatorio"));
            }
            if (precio == null || precio <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("El precio debe ser mayor a 0"));
            }
            if (categoriaId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("La categoría es obligatoria"));
            }
            if (imagenFile == null || imagenFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La imagen es obligatoria"));
            }

            // Validar stock (puede ser 0 o positivo)
            if (stock != null && stock < 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("El stock no puede ser negativo"));
            }

            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre(nombre.trim());
            nuevoProducto.setDescripcion(descripcion != null ? descripcion.trim() : "Sin descripción");
            nuevoProducto.setPrecio(precio);
            nuevoProducto.setStock(stock != null ? stock : 0); // Permitir 0

            var categoriaOpt = categoriaService.obtenerCategoriaPorId(categoriaId);
            if (categoriaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Categoría no encontrada"));
            }
            nuevoProducto.setCategoria(categoriaOpt.get());

            Producto productoGuardado = productoService.guardarProducto(nuevoProducto, imagenFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado con éxito");
            response.put("data", productoGuardado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Método auxiliar para respuestas de error
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    @PostMapping("/api/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarProducto(
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

            boolean hasChanges = false;

            // Validar stock si se proporciona
            if (stock != null && stock < 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("El stock no puede ser negativo"));
            }

            // Actualizar campos...
            if (nombre != null && !nombre.trim().isEmpty()) {
                producto.setNombre(nombre.trim());
                hasChanges = true;
            }

            if (descripcion != null) {
                producto.setDescripcion(descripcion.trim());
                hasChanges = true;
            }

            if (precio != null && precio > 0) {
                producto.setPrecio(precio);
                hasChanges = true;
            }

            if (stock != null) {
                producto.setStock(stock); // Permite 0
                hasChanges = true;
            }

            if (categoriaId != null) {
                var categoriaOpt = categoriaService.obtenerCategoriaPorId(categoriaId);
                if (categoriaOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Categoría no encontrada"));
                }
                producto.setCategoria(categoriaOpt.get());
                hasChanges = true;
            }

            if (imagenFile != null && !imagenFile.isEmpty()) {
                hasChanges = true;
            }

            if (!hasChanges) {
                return ResponseEntity.badRequest().body(createErrorResponse("No se realizaron cambios"));
            }

            Producto productoActualizado = productoService.guardarProducto(producto, imagenFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto actualizado con éxito");
            response.put("data", productoActualizado);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar: " + e.getMessage()));
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

    @GetMapping("/api/listar-productos-categoria/{id}")
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
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            productoService.eliminarProducto(id);
            response.put("success", true);
            response.put("message", "Producto eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

