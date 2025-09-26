-- Inserciones de datos iniciales para opciones
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
                                                   (1, 'Dashboard', '/', 'home'),
                                                   (2, 'Gestión de Usuarios', '/usuarios/listar', 'users'),
                                                   (3, 'Gestión de Perfiles', '/perfiles/listar', 'shield');

-- Inserciones de datos iniciales para perfiles
INSERT INTO perfiles (id, nombre, descripcion, estado) VALUES
                                                           (1, 'Administrador', 'Acceso total al sistema.', 1);

-- Inserciones de datos iniciales para perfil_opcion
INSERT INTO perfil_opcion (id_perfil, id_opcion) VALUES
                                                     (1, 1),
                                                     (1, 2),
                                                     (1, 3);

-- Inserciones de datos iniciales para usuarios
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil) VALUES
                                                                                 (1, 'AdministradorBase', 'SuperAdmin', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'administrador@gmail.com', 1, 1);
