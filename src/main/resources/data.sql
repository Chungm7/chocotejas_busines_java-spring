-- Inserciones de datos iniciales para opciones
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
                                                   (1, 'Dashboard', '/', 'home'),
                                                   (2, 'Gestión de Usuarios', '/usuarios/listar', 'users'),
                                                   (3, 'Gestión de Perfiles', '/perfiles/listar', 'shield');

-- Inserciones de datos iniciales para perfiles
INSERT INTO perfiles (id, nombre, descripcion, estado) VALUES
                                                           (1, 'Administrador', 'Acceso total al sistema.', 1),
                                                           (2, 'Editor', 'Puede gestionar usuarios pero no perfiles.', 1),
                                                           (3, 'Supervisor', 'Solo puede visualizar información.', 0);

-- Inserciones de datos iniciales para perfil_opcion
INSERT INTO perfil_opcion (id_perfil, id_opcion) VALUES
                                                     (1, 1),
                                                     (1, 2),
                                                     (1, 3),
                                                     (2, 1),
                                                     (2, 2),
                                                     (3, 1);

-- Inserciones de datos iniciales para usuarios
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil) VALUES
                                                                                 (8, 'Daryl', 'admin', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'luis1@ejemplo.com', 1, 1),
                                                                                 (10, 'María Supervisor', 'supervisor', '$2a$10$N9qo8uLOickgx2ZMRZoMye5aZl8ZzO8Fns2h0eCZgP2h7ZWCpU9/y', 'supervisor@ejemplo.com', 1, 3),
                                                                                 (11, 'Carlos Analista', 'analista', '$2a$10$N9qo8uLOickgx2ZMRZoMye5aZl8ZzO8Fns2h0eCZgP2h7ZWCpU9/y', 'analista@ejemplo.com', 0, 2),
                                                                                 (14, 'Luis Antonio', 'luis', '$2a$10$bDRnfg7TQgcBeV.e0cd.ZuNfDUGfPRPhp62tfLVtycqwV/unM0VWm', 'luis@ejemplo.com', 1, 1),
                                                                                 (15, 'Blanca Rosa', 'blanca', '$2a$10$UTJNtLoen3wHnh1WMF756uBNJo9Gm4Hlmm8XuiFTOrJy5wdnt1d3C', 'blanca@ejemplo.com', 0, 2);
