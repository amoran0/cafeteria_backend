

DROP TABLE IF EXISTS pedido_producto, pedido_cupon, pedido, descuento, cupon,
    producto_alergeno, alergeno, producto, categoria, cliente, usuario CASCADE;
DROP TYPE IF EXISTS estado_pedido, turno_pedido, rol_usuario, tipo_cupon CASCADE;

CREATE TYPE estado_pedido AS ENUM ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO');
CREATE TYPE rol_usuario AS ENUM ('ADMIN', 'BARISTA', 'CAMARERO', 'CLIENTE');
CREATE TYPE tipo_cupon AS ENUM ('FIJO', 'PORCENTAJE');


CREATE TABLE categoria (
    id_categoria UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE producto (
    id_producto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_categoria UUID NOT NULL,
    nombre VARCHAR NOT NULL,
    descripcion TEXT,
    precio_base DECIMAL,
    precio_venta DECIMAL NOT NULL,          -- IVA (10 %) incluido, RN-05
    disponible BOOLEAN DEFAULT TRUE,        -- agotado / disponible
    activa BOOLEAN DEFAULT TRUE,            -- baja lógica, RN-01
    iva DECIMAL,

    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE alergeno (
    id_alergeno UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR NOT NULL
);

CREATE TABLE producto_alergeno (
    id_producto UUID,
    id_alergeno UUID,

    PRIMARY KEY (id_producto, id_alergeno),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_alergeno) REFERENCES alergeno(id_alergeno)
);

CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rol rol_usuario NOT NULL DEFAULT 'CLIENTE',
    mail VARCHAR UNIQUE NOT NULL,
    contrasena VARCHAR NOT NULL
);

CREATE TABLE cliente (
    id_cliente UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID UNIQUE NOT NULL,
    nombre VARCHAR NOT NULL,
    mail VARCHAR NOT NULL,
    dni VARCHAR UNIQUE NOT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE cupon (
    id_cupon UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR UNIQUE NOT NULL,
    tipo tipo_cupon NOT NULL,
    valor DECIMAL,
    importe_min DECIMAL,
    max_usos INTEGER,
    fecha_inicio DATE,
    fecha_fin DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    email VARCHAR
);

CREATE TABLE pedido (
    id_pedido UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_cliente UUID NOT NULL,
    id_cupon UUID,
    estado estado_pedido NOT NULL DEFAULT 'PENDIENTE',
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    numero_turno INTEGER NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    base_imponible DECIMAL(10,2) NOT NULL,
    cuota_iva DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_cupon) REFERENCES cupon(id_cupon)
);

CREATE TABLE pedido_producto (
    id_pedido UUID,
    id_producto UUID,
    cantidad INTEGER NOT NULL CHECK (cantidad BETWEEN 1 AND 20),
    precio_unitario DECIMAL NOT NULL,

    PRIMARY KEY (id_pedido, id_producto),
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);


-- 1. Usuarios
INSERT INTO usuario (id_usuario, rol, mail, contrasena) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ADMIN', 'gerencia@cafeteria.com', 'admin123'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'BARISTA', 'marcos.barista@cafeteria.com', 'hash_pass_barista1'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'CAMARERO', 'lucia.camarera@cafeteria.com', 'hash_pass_camarero2'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'CLIENTE', 'carlos.gomez@gmail.com', 'hash_pass_123'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'CLIENTE', 'maria.lopez@yahoo.com', 'hash_pass_456'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'CLIENTE', 'juan.perez@hotmail.com', 'hash_pass_789'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'CLIENTE', 'ana.martinez@gmail.com', 'hash_pass_abc'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'CLIENTE', 'luis.fernandez@outlook.com', 'hash_pass_def'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'CLIENTE', 'laura.sanchez@gmail.com', 'hash_pass_ghi'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'CLIENTE', 'diego.ramirez@yahoo.com', 'hash_pass_jkl'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'CLIENTE', 'marta.ruiz@gmail.com', 'hash_pass_paso1'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'CLIENTE', 'pedro.a@gmail.com', 'hash_pass_paso2'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'CLIENTE', 'elena.p@gmail.com', 'hash_pass_paso3');

-- 2. Clientes
INSERT INTO cliente (id_cliente, id_usuario, nombre, mail, dni) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Carlos Gómez', 'carlos.gomez@gmail.com', '12345678A'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'María López', 'maria.lopez@yahoo.com', '87654321B'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'Juan Pérez', 'juan.perez@hotmail.com', '11223344C'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'Ana Martínez', 'ana.martinez@gmail.com', '44332211D'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'Luis Fernández', 'luis.fernandez@outlook.com', '55667788E'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'Laura Sánchez', 'laura.sanchez@gmail.com', '99887766F'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'Diego Ramírez', 'diego.ramirez@yahoo.com', '22334455G'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Marta Ruiz (Paso)', 'marta.ruiz@gmail.com', '33445566H'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Pedro Alarcón (Paso)', 'pedro.a@gmail.com', '77889900I'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'Elena Prieto (Paso)', 'elena.p@gmail.com', '55443322J');

-- 3. Cupones
INSERT INTO cupon (id_cupon, codigo, tipo, valor, importe_min, max_usos, fecha_inicio, fecha_fin, activo, email) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'CAFEMATIN', 'FIJO', 1, 5.00, 100, '2026-01-01', '2026-12-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'DULCE20', 'PORCENTAJE', 20, 8.00, 50, '2026-03-01', '2026-05-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'PROMOBATIDO', 'FIJO', 2, 10.00, 30, '2026-06-01', '2026-08-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'BIENVENIDACAFE', 'PORCENTAJE', 15, 4.00, 200, '2026-01-01', '2026-12-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'TAKEAWAY10', 'PORCENTAJE', 10, 6.00, 150, '2026-02-01', '2026-11-30', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'MERIENDAVIP', 'FIJO', 3, 12.00, 40, '2026-01-01', '2026-12-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'CUMPLECAFEE', 'PORCENTAJE', 50, 0.00, 10, '2026-01-01', '2026-12-31', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'DESAYUNO2X1', 'PORCENTAJE', 25, 7.00, 80, '2026-04-01', '2026-06-30', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'VERANOFRIO', 'FIJO', 1, 4.50, 100, '2026-06-15', '2026-09-15', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'CROSSANTFREE', 'FIJO', 2, 8.00, 60, '2026-10-01', '2026-10-31', TRUE, NULL),
-- cupón personal (RN-06): solo lo puede usar María López
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SOLOMARIA', 'PORCENTAJE', 30, 0.00, 1, '2026-01-01', '2026-12-31', TRUE, 'maria.lopez@yahoo.com');

-- 4. Categorías
INSERT INTO categoria (id_categoria, nombre, disponible) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Cafés Clásicos', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Cafés Especiales y Fríos', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Tés e Infusiones', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Tostadas y Salados', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Bollería Artesanal', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Tartas y Repostería', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Zumos y Batidos Naturales', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Combos Desayuno', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Bebidas Vegetales / Extras', TRUE),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Especiales de Temporada', FALSE);

-- 5. Productos
INSERT INTO producto (id_producto, id_categoria, nombre, descripcion, precio_venta, disponible, activa, iva) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Café Espresso', '100% Arábica de tueste natural', 1.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Café con Leche', 'Espresso largo con leche vaporizada', 1.80, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Iced Caramel Macchiato', 'Café frío con hielo, leche, vainilla y caramelo', 3.80, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Cappuccino Italiano', 'Espresso, leche vaporizada y espuma de leche con cacao', 2.80, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Tostada con Tomate y Aceite', 'Pan de masa madre tostado con tomate natural y AOVE', 2.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Tostada de Aguacate y Huevo', 'Pan de semillas, aguacate laminado y huevo poché', 4.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Croissant de Mantequilla', 'Hornear diario, hojaldrado artesanal', 1.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Tarta de Zanahoria (Carrot Cake)', 'Porción casera con cobertura de queso crema', 4.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Zumo de Naranja Natural', 'Recién exprimido (250ml)', 2.80, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Smoothie Antiox', 'Batido de frutos rojos, plátano y manzana', 3.90, TRUE, TRUE, 10);

-- 6. Alérgenos
INSERT INTO alergeno (id_alergeno, nombre) VALUES
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Gluten'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Lácteos'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Huevos'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Frutos de cáscara'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Soja'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Sésamo'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Sulfito'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Mostaza'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Cacahuetes'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Altramuces');

-- 7. Producto-Alérgeno
INSERT INTO producto_alergeno (id_producto, id_alergeno) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04');

-- 8. Pedidos (importes calculados según RN-05; turno según RN-09)
INSERT INTO pedido (id_pedido, id_cliente, id_cupon, estado, fecha, numero_turno, subtotal, descuento, total, base_imponible, cuota_iva) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'ENTREGADO',      '2026-10-04 08:30:00',  1,  4.30, 0.65,  3.65,  3.32, 0.33),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'ENTREGADO',      '2026-10-04 09:15:00',  2, 11.40, 1.00, 10.40,  9.45, 0.95),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'ENTREGADO',      '2026-10-04 10:00:00',  3,  7.70, 0.77,  6.93,  6.30, 0.63),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', NULL,                                   'ENTREGADO',      '2026-10-04 11:30:00',  4,  3.00, 0.00,  3.00,  2.73, 0.27),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', NULL,                                   'CANCELADO',      '2026-10-04 12:00:00',  5,  3.90, 0.00,  3.90,  3.55, 0.35),
-- pedido 6: descuento de socio FitZone (10 %), sin cupón
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', NULL,                                   'ENTREGADO',      '2026-10-04 16:30:00',  6,  7.00, 0.70,  6.30,  5.73, 0.57),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', NULL,                                   'EN_PREPARACION', '2026-10-04 17:15:00',  7,  3.80, 0.00,  3.80,  3.45, 0.35),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'PENDIENTE',      '2026-10-04 17:30:00',  8,  6.80, 1.00,  5.80,  5.27, 0.53),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'ENTREGADO',      '2026-10-04 18:00:00',  9, 15.40, 3.00, 12.40, 11.27, 1.13),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'LISTO',          '2026-10-04 18:45:00', 10,  8.70, 2.00,  6.70,  6.09, 0.61);

-- 9. Líneas de pedido
INSERT INTO pedido_producto (id_pedido, id_producto, cantidad, precio_unitario) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 1, 1.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 1, 2.50),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 2, 3.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 2, 1.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 1, 4.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 1, 2.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 2, 1.50),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 1, 3.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 1, 2.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 1, 4.20),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 1, 3.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 2, 2.50),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 1, 1.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 2, 4.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 2, 2.80),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 3, 1.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 2, 1.50);
