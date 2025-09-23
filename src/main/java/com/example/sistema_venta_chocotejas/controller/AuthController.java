package com.example.sistema_venta_chocotejas.controller;

import com.example.sistema_venta_chocotejas.dto.AuthenticationRequest;
import com.example.sistema_venta_chocotejas.dto.AuthenticationResponse;
import com.example.sistema_venta_chocotejas.model.Usuario;
import com.example.sistema_venta_chocotejas.service.UsuarioService;
import com.example.sistema_venta_chocotejas.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.username());

        final String jwt = jwtUtil.generateToken(userDetails);

        Usuario usuario = usuarioService.findByUsuario(authenticationRequest.username()).get();

        return ResponseEntity.ok(new AuthenticationResponse(jwt, usuario));
    }
}
