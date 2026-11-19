 

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


-- =====================================================================
-- 10. Datos adicionales (50 filas)
--     4 alérgenos + 12 productos + 13 producto_alergeno + 5 usuarios
--     + 4 clientes + 2 cupones + 4 pedidos + 6 líneas
--     Los pedidos nuevos son de otros días: el 04/10 sigue con sus 10 pedidos.
-- =====================================================================

-- 10.1 Alérgenos que faltaban para completar los 14 de declaración obligatoria (4)
INSERT INTO alergeno (id_alergeno, nombre) VALUES
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Pescado'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Crustáceos'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Moluscos'),
('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Apio');

-- 10.2 Productos para las categorías que estaban vacías o con poca oferta (12)
INSERT INTO producto (id_producto, id_categoria, nombre, descripcion, precio_base, precio_venta, disponible, activa, iva) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Té Verde Matcha', 'Matcha ceremonial batido con agua a 80 ºC', 0.70, 2.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Rooibos con Canela', 'Infusión sin teína con canela de Ceilán', 0.45, 1.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Chai Latte', 'Té negro especiado con leche vaporizada', 0.90, 3.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Napolitana de Chocolate', 'Hojaldre de mantequilla relleno de chocolate negro', 0.60, 2.10, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Muffin de Arándanos', 'Magdalena esponjosa con arándanos frescos', 0.70, 2.40, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Cheesecake de Frutos Rojos', 'Tarta de queso al horno con coulis de frutos rojos', 1.40, 4.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Brownie de Chocolate y Nueces', 'Bizcocho denso de chocolate con nueces', 1.00, 3.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Desayuno Completo', 'Café con leche, zumo de naranja y tostada con tomate', 1.90, 5.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Combo Dulce', 'Café a elegir y croissant de mantequilla', 1.00, 3.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Suplemento Bebida de Avena', 'Cambia la leche por bebida de avena', 0.15, 0.40, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Suplemento Bebida de Soja', 'Cambia la leche por bebida de soja', 0.15, 0.40, TRUE, TRUE, 10),
-- categoría de temporada agotada: el producto también está agotado (RN-02)
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Pumpkin Spice Latte', 'Espresso con leche, calabaza y especias de otoño', 1.10, 3.90, FALSE, TRUE, 10);

-- 10.3 Alérgenos de los productos nuevos (13)
INSERT INTO producto_alergeno (id_producto, id_alergeno) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a05'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02');

-- 10.4 Usuarios: una barista de tarde y cuatro clientes nuevos (5)
INSERT INTO usuario (id_usuario, rol, mail, contrasena) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'BARISTA', 'sofia.barista@cafeteria.com', 'hash_pass_barista3'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'CLIENTE', 'javier.molina@gmail.com', 'hash_pass_mno'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'CLIENTE', 'carmen.ortiz@hotmail.com', 'hash_pass_pqr'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'CLIENTE', 'raul.navarro@outlook.com', 'hash_pass_stu'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'CLIENTE', 'irene.castro@gmail.com', 'hash_pass_vwx');

-- 10.5 Clientes de esos usuarios (4)
INSERT INTO cliente (id_cliente, id_usuario, nombre, mail, dni) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'Javier Molina', 'javier.molina@gmail.com', '48291037F'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'Carmen Ortiz', 'carmen.ortiz@hotmail.com', '50716284L'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'Raúl Navarro', 'raul.navarro@outlook.com', '29384756W'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'Irene Castro', 'irene.castro@gmail.com', '71625038W');

-- 10.6 Cupones de temporada (2): OTONO10 vigente, NAVIDAD2 aún no ha empezado (RN-06)
INSERT INTO cupon (id_cupon, codigo, tipo, valor, importe_min, max_usos, fecha_inicio, fecha_fin, activo, email) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'OTONO10', 'PORCENTAJE', 10, 3.00, 100, '2026-09-22', '2026-12-20', TRUE, NULL),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'NAVIDAD2', 'FIJO', 2, 10.00, 50, '2026-12-01', '2027-01-06', TRUE, NULL);

-- 10.7 Pedidos de otros días (4). Turno por día (RN-09) e importes según RN-05
INSERT INTO pedido (id_pedido, id_cliente, id_cupon, estado, fecha, numero_turno, subtotal, descuento, total, base_imponible, cuota_iva) VALUES
-- 1 desayuno completo = 5.90
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NULL,                                   'ENTREGADO', '2026-10-05 08:10:00', 1, 5.90, 0.00, 5.90, 5.36, 0.54),
-- chai 3.20 + cheesecake 4.50 = 7.70; OTONO10 -10 % = 0.77
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'ENTREGADO', '2026-10-05 09:40:00', 2, 7.70, 0.77, 6.93, 6.30, 0.63),
-- 2 matchas = 4.40, cancelado
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', NULL,                                   'CANCELADO', '2026-10-05 11:15:00', 3, 4.40, 0.00, 4.40, 4.00, 0.40),
-- muffin 2.40 + café con leche 1.80 = 4.20; BIENVENIDACAFE -15 % = 0.63
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'ENTREGADO', '2026-11-18 10:30:00', 1, 4.20, 0.63, 3.57, 3.25, 0.32);

-- 10.8 Líneas de esos pedidos (6)
INSERT INTO pedido_producto (id_pedido, id_producto, cantidad, precio_unitario) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18', 1, 5.90),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 1, 3.20),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 1, 4.50),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 2, 2.20),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 1, 2.40),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 1, 1.80);


-- =====================================================================
-- 11. Ampliación de la carta: 30 productos (3 por categoría) y sus alérgenos
-- =====================================================================
INSERT INTO producto (id_producto, id_categoria, nombre, descripcion, precio_base, precio_venta, disponible, activa, iva) VALUES
-- Cafés Clásicos
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Café Cortado', 'Espresso con un toque de leche', 0.45, 1.60, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Café Americano', 'Espresso alargado con agua caliente', 0.45, 1.70, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Café Bombón', 'Espresso sobre leche condensada', 0.55, 1.90, TRUE, TRUE, 10),
-- Cafés Especiales y Fríos
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Flat White', 'Doble ristretto con leche microespumada', 0.85, 3.00, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Mocha Blanco', 'Espresso, chocolate blanco y leche vaporizada', 1.00, 3.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Cold Brew', 'Café infusionado en frío durante 18 horas, con hielo', 0.80, 3.20, TRUE, TRUE, 10),
-- Tés e Infusiones
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Té Earl Grey', 'Té negro con aceite de bergamota', 0.40, 1.80, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Poleo Menta', 'Infusión digestiva de poleo y menta', 0.30, 1.60, TRUE, TRUE, 10),
-- descatalogado: baja lógica (RN-01), no debe salir en la carta
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Té Rojo Pu-erh', 'Té fermentado de Yunnan', 0.50, 1.90, TRUE, FALSE, 10),
-- Tostadas y Salados
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Tostada de Jamón Ibérico', 'Pan de masa madre con tomate, AOVE y jamón ibérico', 1.50, 4.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Sándwich Mixto', 'Pan de molde tostado con jamón cocido y queso', 1.00, 3.50, TRUE, TRUE, 10),
-- agotado hoy (RN-02) aunque su categoría está disponible
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Bagel de Salmón', 'Bagel de sésamo con salmón ahumado y queso crema', 2.00, 5.50, FALSE, TRUE, 10),
-- Bollería Artesanal
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Ensaimada', 'Ensaimada mallorquina espolvoreada con azúcar glas', 0.65, 2.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Palmera de Chocolate', 'Hojaldre caramelizado bañado en chocolate', 0.70, 2.30, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05', 'Croissant Integral con Mermelada', 'Croissant de harina integral con mermelada de fresa', 0.65, 2.20, TRUE, TRUE, 10),
-- Tartas y Repostería
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Tarta de Santiago', 'Tarta de almendra gallega, sin harina', 1.20, 3.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Tarta de Chocolate', 'Bizcocho de cacao con ganache de chocolate negro', 1.30, 4.20, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a06', 'Tarta de Manzana', 'Masa quebrada con compota y láminas de manzana', 1.10, 3.80, TRUE, TRUE, 10),
-- Zumos y Batidos Naturales
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Zumo Verde Detox', 'Manzana verde, apio, pepino, espinaca y limón', 1.10, 3.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Batido de Plátano y Avena', 'Plátano, copos de avena, leche y canela', 1.10, 3.60, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a07', 'Limonada Casera', 'Limón exprimido, hierbabuena y sirope de caña', 0.60, 2.50, TRUE, TRUE, 10),
-- Combos Desayuno
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Desayuno Saludable', 'Café, tostada de aguacate y huevo y zumo de naranja', 2.60, 7.90, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Combo Merienda', 'Café o té y una porción de tarta a elegir', 1.80, 5.50, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a46', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a08', 'Combo Salado', 'Café y sándwich mixto', 1.50, 4.90, TRUE, TRUE, 10),
-- Bebidas Vegetales / Extras
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a47', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Suplemento Bebida de Almendra', 'Cambia la leche por bebida de almendra', 0.15, 0.40, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a48', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Suplemento Shot de Espresso', 'Carga extra de café en cualquier bebida', 0.20, 0.60, TRUE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a49', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a09', 'Suplemento Nata Montada', 'Nata montada por encima', 0.15, 0.50, TRUE, TRUE, 10),
-- Especiales de Temporada: la categoría está agotada, así que sus productos también (RN-02)
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Chocolate con Churros', 'Chocolate a la taza con ración de churros', 1.40, 4.50, FALSE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Porción de Roscón de Reyes', 'Roscón relleno de nata con fruta escarchada', 1.10, 3.50, FALSE, TRUE, 10),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a52', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a10', 'Horchata Valenciana', 'Horchata de chufa artesana bien fría', 0.80, 2.80, FALSE, TRUE, 10);

-- 01 Gluten · 02 Lácteos · 03 Huevos · 04 Frutos de cáscara · 06 Sésamo · 11 Pescado · 14 Apio
INSERT INTO producto_alergeno (id_producto, id_alergeno) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a06'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a46', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a46', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a47', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a49', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03'),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a04');
