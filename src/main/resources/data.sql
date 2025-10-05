-- Inserciones de datos iniciales para opciones
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
                                                   (1, 'Dashboard', '/gestion/dashboard/mostrar', 'bi bi-speedometer2'),
                                                   (2, 'Gestión de Usuarios', '/gestion/usuarios/listar', 'bi bi-people'),
                                                   (3, 'Gestión de Perfiles', '/gestion/perfiles/listar', 'bi bi-shield-lock'),
                                                   (4, 'Gestión de Categorías', '/gestion/categorias/listar', 'bi bi-tags'),
                                                   (5, 'Gestión de Productos', '/gestion/productos/listar', 'bi bi-box-seam'),
                                                   (6, 'Gestión de Ventas', '/gestion/ventas/listar', 'bi bi-cash-stack'),
                                                   (7, 'Gestión de Slider', '/gestion/sliders/listar', 'bi bi-images'),
                                                   (8, 'Gestión de Logo', '/gestion/logos/listar', 'bi bi-image'),
                                                   (9, 'Gestión de Momentos', '/gestion/momentos/listar', 'bi bi-camera-reels'),
                                                   (10, 'Gestion de Redes Sociales', '/gestion/redes-sociales/listar', 'bi bi-share'),
                                                   (11, 'Gestión de Contacto', '/gestion/contacto/mostrar', 'bi bi-envelope'),
                                                   (12, 'Gestion de Inicio', '/gestion/inicio/mostrar', 'bi bi-house');


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
                                                     (1, 6),
                                                     (1, 7),
                                                     (1, 8),
                                                     (1, 9),
                                                     (1, 10),
                                                     (1, 11),
                                                     (1, 12);

-- Inserciones de datos iniciales para usuarios
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil) VALUES
                                                                                 (1, 'AdministradorBase', 'SuperAdmin', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'administrador@gmail.com', 1, 1);

-- Inserciones de datos iniciales para categorias
INSERT INTO categorias (id, nombre, descripcion, estado) VALUES
                                                             (1, 'Chocolate Negro', 'Productos elaborados con chocolate negro de alta calidad', 1),
                                                             (2, 'Chocolate con Leche', 'Deliciosos chocolates cremosos con leche', 1),
                                                             (3, 'Chocolate Blanco', 'Suaves y dulces chocolates blancos premium', 1);
INSERT INTO inicio (titulo, contenido)
VALUES ('Bienvenidos a Chocotejas Delicias',
        'Disfruta de nuestras chocotejas artesanales elaboradas con los mejores ingredientes del Valle del Mantaro. ¡Dulces momentos que alegran tu día!');

INSERT INTO contactos (telefono, direccion, email, descripcion)
VALUES ('+51 987 654 321',
        'Av. Mariscal Castilla 456, Huancayo - Junín',
        'contacto@chocotejasdelicias.com',
        'Atendemos pedidos personalizados y envíos a todo el país. ¡Contáctanos para endulzar tus momentos especiales!');