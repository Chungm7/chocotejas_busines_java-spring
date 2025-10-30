package com.example.sistema_venta_chocotejas.controller.gestion;

import com.example.sistema_venta_chocotejas.dto.MovimientoInventarioDTO;
import com.example.sistema_venta_chocotejas.dto.ProductoDTO;
import com.example.sistema_venta_chocotejas.model.MovimientoInventario;
import com.example.sistema_venta_chocotejas.service.Impl.MovimientoInventarioServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gestion/inventario")
public class InventarioController {

    private final MovimientoInventarioServiceImpl movimientoInventarioService;

    public InventarioController(MovimientoInventarioServiceImpl movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaInventario() {
        return "gestion/gestion-inventario";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarMovimientos() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MovimientoInventario> movimientos = movimientoInventarioService.listarTodosLosMovimientos();
            List<MovimientoInventarioDTO> movimientosDTO = movimientos.stream()
                    .map(this::convertirAMovimientoDTO)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", movimientosDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al listar movimientos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/producto/{productoId}")
    @ResponseBody
    public ResponseEntity<?> listarMovimientosPorProducto(@PathVariable Long productoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MovimientoInventario> movimientos = movimientoInventarioService.listarMovimientosPorProducto(productoId);
            List<MovimientoInventarioDTO> movimientosDTO = movimientos.stream()
                    .map(this::convertirAMovimientoDTO)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", movimientosDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al listar movimientos del producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private MovimientoInventarioDTO convertirAMovimientoDTO(MovimientoInventario movimiento) {
        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();
        dto.setId(movimiento.getId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setStockAnterior(movimiento.getStockAnterior());
        dto.setNuevoStock(movimiento.getNuevoStock());
        dto.setDiferencia(movimiento.getDiferencia());
        dto.setComprobante(movimiento.getComprobante());
        dto.setObservaciones(movimiento.getObservaciones());

        // Convertir producto a DTO
        if (movimiento.getProducto() != null) {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(movimiento.getProducto().getId());
            productoDTO.setNombre(movimiento.getProducto().getNombre());
            productoDTO.setDescripcion(movimiento.getProducto().getDescripcion());
            productoDTO.setPrecio(movimiento.getProducto().getPrecio());
            productoDTO.setStock(movimiento.getProducto().getStock());
            productoDTO.setImagen(movimiento.getProducto().getImagen());
            productoDTO.setEstado(movimiento.getProducto().getEstado());
            productoDTO.setDestacado(movimiento.getProducto().getDestacado());

            dto.setProducto(productoDTO);
        }

        return dto;
    }
}