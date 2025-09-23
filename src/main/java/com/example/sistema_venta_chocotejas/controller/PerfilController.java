package com.example.sistema_venta_chocotejas.controller;

import com.example.sistema_venta_chocotejas.model.Opcion;
import com.example.sistema_venta_chocotejas.model.Perfil;
import com.example.sistema_venta_chocotejas.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/perfiles")
@CrossOrigin(origins = "*")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<List<Perfil>> listarPerfiles() {
        return ResponseEntity.ok(perfilService.listarTodosLosPerfiles());
    }

    @GetMapping("/activos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Perfil>> listarPerfilesActivos() {
        return ResponseEntity.ok(perfilService.listarPerfilesActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Perfil> obtenerPerfil(@PathVariable Long id) {
        return perfilService.obtenerPerfilPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Perfil> guardarPerfil(@RequestBody Perfil perfil) {
        Perfil perfilGuardado = perfilService.guardarPerfil(perfil);
        return ResponseEntity.ok(perfilGuardado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Void> eliminarPerfil(@PathVariable Long id) {
        perfilService.eliminarPerfil(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Perfil> cambiarEstadoPerfil(@PathVariable Long id) {
        return perfilService.cambiarEstadoPerfil(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/opciones")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<List<Opcion>> listarOpciones() {
        return ResponseEntity.ok(perfilService.listarTodasLasOpciones());
    }
}