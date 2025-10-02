package com.example.sistema_venta_chocotejas.service.Impl;

import com.example.sistema_venta_chocotejas.model.Usuario;
import com.example.sistema_venta_chocotejas.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByEstadoNot(2);
    }

    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        try {
            // Validaciones adicionales
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }

            if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
                throw new IllegalArgumentException("El usuario es obligatorio");
            }

            if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
                throw new IllegalArgumentException("El correo es obligatorio");
            }

            // Validar que tenga un perfil asignado
            if (usuario.getPerfil() == null || usuario.getPerfil().getId() == null) {
                throw new IllegalArgumentException("El perfil es obligatorio");
            }

            // Normalizar datos - MANTENER CASE-SENSITIVE para usuario
            usuario.setNombre(usuario.getNombre().trim());
            usuario.setUsuario(usuario.getUsuario().trim()); // Eliminado .toLowerCase()
            usuario.setCorreo(usuario.getCorreo().trim().toLowerCase()); // Correo sí en minúsculas

            // Manejo de contraseñas
            if (usuario.getId() != null) {
                // Usuario existente - actualización
                Optional<Usuario> usuarioExistente = obtenerUsuarioPorId(usuario.getId());
                if (usuarioExistente.isPresent()) {
                    // Si no se proporciona nueva contraseña, mantener la actual
                    if (usuario.getClave() == null || usuario.getClave().trim().isEmpty()) {
                        usuario.setClave(usuarioExistente.get().getClave());
                    } else {
                        // Encriptar nueva contraseña
                        usuario.setClave(passwordEncoder.encode(usuario.getClave().trim()));
                    }
                } else {
                    throw new IllegalArgumentException("Usuario no encontrado para actualizar");
                }
            } else {
                // Nuevo usuario
                if (usuario.getClave() == null || usuario.getClave().trim().isEmpty()) {
                    throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios");
                }
                // Encriptar contraseña
                usuario.setClave(passwordEncoder.encode(usuario.getClave().trim()));
                // Asignar estado activo por defecto a nuevos usuarios
                usuario.setEstado(1);
            }

            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException e) {
            // Manejar violaciones de restricciones únicas
            String message = e.getMessage().toLowerCase();
            if (message.contains("usuario")) {
                throw new IllegalArgumentException("El nombre de usuario ya existe");
            } else if (message.contains("correo") || message.contains("email")) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado");
            } else if (message.contains("perfil")) {
                throw new IllegalArgumentException("Error en la asignación del perfil");
            } else {
                throw new IllegalArgumentException("Error de integridad de datos");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return usuarioRepository.countByEstadoNot(2);
    }

    @Transactional
    public long contarUsuariosActivos() {
        return usuarioRepository.countByEstado(1);
    }

    @Transactional
    public long contarUsuariosInactivos() {
        return usuarioRepository.countByEstado(0);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsuario(String usuario) {
        // Eliminado .toLowerCase() para mantener case-sensitive
        return usuarioRepository.findByUsuario(usuario.trim());
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        Usuario usuario = obtenerUsuarioPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEstado(2);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario> cambiarEstadoUsuario(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return obtenerUsuarioPorId(id).map(usuario -> {
            if (usuario.getEstado() == 1) {
                usuario.setEstado(0);
            } else if (usuario.getEstado() == 0) {
                usuario.setEstado(1);
            }
            return usuarioRepository.save(usuario);
        });
    }

    /**
     * Verifica si un nombre de usuario ya existe
     */
    @Transactional(readOnly = true)
    public boolean existeUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }
        // Eliminado .toLowerCase() para mantener case-sensitive
        return usuarioRepository.existsByUsuario(nombreUsuario.trim());
    }

    /**
     * Verifica si un correo ya existe
     */
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByCorreo(correo.trim().toLowerCase());
    }

    /**
     * Verifica la contraseña de un usuario
     */
    public boolean verificarContrasena(String contrasenaTextoPlano, String contrasenaEncriptada) {
        return passwordEncoder.matches(contrasenaTextoPlano, contrasenaEncriptada);
    }
}