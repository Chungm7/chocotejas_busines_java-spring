-- ======================================
-- OPCIONES (menús o accesos del sistema)
-- ======================================
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
                                                   (1, 'Dashboard', '/dashboard', 'fa-home'),
                                                   (2, 'Gestión de Usuarios', '/usuarios', 'fa-users'),
                                                   (3, 'Gestión de Productos', '/productos', 'fa-box'),
                                                   (4, 'Gestión de Ventas', '/ventas', 'fa-shopping-cart'),
                                                   (5, 'Reportes', '/reportes', 'fa-chart-bar');

-- ======================================
-- PERFILES (roles)
-- ======================================
INSERT INTO perfiles (id, nombre, descripcion, estado) VALUES
                                                           (1, 'Administrador', 'Acceso total al sistema', true),
                                                           (2, 'Vendedor', 'Gestión de ventas y clientes', true),
                                                           (3, 'Cliente', 'Acceso al catálogo y compras', true);

-- ======================================
-- PERFIL_OPCION (relación muchos a muchos)
-- Administrador → todas las opciones
-- Vendedor → ventas + productos + reportes
-- Cliente → solo dashboard + productos
-- ======================================
INSERT INTO perfil_opcion (id_perfil, id_opcion) VALUES
-- Administrador
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
-- Vendedor
(2, 1), (2, 3), (2, 4), (2, 5),
-- Cliente
(3, 1), (3, 3);

-- ======================================
-- USUARIOS (10 registros)
-- ======================================
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil) VALUES
                                                                                 (1, 'Juan Pérez', 'admin1', '123456', 'admin1@correo.com', 1, 1),
                                                                                 (2, 'María López', 'admin2', '123456', 'admin2@correo.com', 1, 1),
                                                                                 (3, 'Carlos Ruiz', 'vend1', '123456', 'vend1@correo.com', 1, 2),
                                                                                 (4, 'Ana Torres', 'vend2', '123456', 'vend2@correo.com', 1, 2),
                                                                                 (5, 'Pedro Gómez', 'vend3', '123456', 'vend3@correo.com', 1, 2),
                                                                                 (6, 'Luis Silva', 'cli1', '123456', 'cli1@correo.com', 1, 3),
                                                                                 (7, 'Gabriela Soto', 'cli2', '123456', 'cli2@correo.com', 1, 3),
                                                                                 (8, 'Andrés Ramos', 'cli3', '123456', 'cli3@correo.com', 1, 3),
                                                                                 (9, 'Mónica Vargas', 'cli4', '123456', 'cli4@correo.com', 1, 3),
                                                                                 (10, 'Diego Fernández', 'cli5', '123456', 'cli5@correo.com', 1, 3);
