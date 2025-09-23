package com.example.sistema_venta_chocotejas.service;

import com.example.sistema_venta_chocotejas.model.Usuario;
import com.example.sistema_venta_chocotejas.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.getPerfil() != null) {
            authorities.add(new SimpleGrantedAuthority(usuario.getPerfil().getNombre()));
        }

        return new User(usuario.getUsuario(), usuario.getClave(), authorities);
    }
}
