package com.example.sistema_venta_chocotejas.controller;

import com.example.sistema_venta_chocotejas.model.Opcion;
import com.example.sistema_venta_chocotejas.model.Usuario;
import com.example.sistema_venta_chocotejas.service.Impl.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.Optional;

@Controller
public class LoginController {
    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/";
        }
        return "gestion-login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String clave, HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // Buscar usuario manteniendo el case-sensitive exacto
        Optional<Usuario> usuarioOpt = usuarioService.findByUsuario(usuario);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }

        Usuario usuarioEncontrado = usuarioOpt.get();

        if (usuarioEncontrado.getEstado() != 1) {
            redirectAttributes.addFlashAttribute("error", "Este usuario se encuentra inactivo.");
            return "redirect:/login";
        }

        // La verificación de contraseña con BCrypt es case-sensitive por defecto
        if (usuarioService.verificarContrasena(clave, usuarioEncontrado.getClave())) {
            session.setAttribute("usuarioLogueado", usuarioEncontrado);

            var opcionesMenu = usuarioEncontrado.getPerfil().getOpciones().stream()
                    .sorted(Comparator.comparing(Opcion::getId))
                    .toList();
            session.setAttribute("menuOpciones", opcionesMenu);

            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logout", "Has cerrado sesión exitosamente.");
        return "redirect:/login";
    }
}