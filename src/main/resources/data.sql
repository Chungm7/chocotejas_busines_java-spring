-- Inserciones de datos iniciales para opciones
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
                                                   (1, 'Dashboard', '/gestion/dashboard/mostrar', 'bi bi-speedometer2'),
                                                   (2, 'Gestión de Usuarios', '/gestion/usuarios/listar', 'bi bi-people'),
                                                   (3, 'Gestión de Perfiles', '/gestion/perfiles/listar', 'bi bi-shield-lock'),
                                                   (4, 'Gestión de Categorías', '/gestion/categorias/listar', 'bi bi-tags'),
                                                   (5, 'Gestión de Productos', '/gestion/productos/listar', 'bi bi-box-seam'),
                                                   (6,'Gestion de Opciones','/gestion/opciones/listar','bi bi-gear');

-- Inserciones de datos iniciales para perfiles
INSERT INTO perfiles (id, nombre, descripcion, estado) VALUES
                                                           (1, 'Administrador', 'Acceso total al sistema.', 1);

-- Inserciones de datos iniciales para perfil_opcion
INSERT INTO perfil_opcion (id_perfil, id_opcion) VALUES
                                                     (1, 1),
                                                     (1, 2),
                                                     (1, 3),
                                                     (1, 4),
                                                     (1, 5),
                                                     (1, 6);

-- Inserciones de datos iniciales para usuarios
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil) VALUES
                                                                                 (1, 'AdministradorBase', 'SuperAdmin', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'administrador@gmail.com', 1, 1);

-- Inserciones de datos iniciales para categorias
INSERT INTO categorias (id, nombre, descripcion, estado) VALUES
                                                             (1, 'Chocolate Negro', 'Productos elaborados con chocolate negro de alta calidad', 1),
                                                             (2, 'Chocolate con Leche', 'Deliciosos chocolates cremosos con leche', 1),
                                                             (3, 'Chocolate Blanco', 'Suaves y dulces chocolates blancos premium', 1);