-- Script de Creación de Tablas - Sistema Restobar
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'MESERO', 'COCINA', 'BARRA')),
    activo BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE IF NOT EXISTS mesas (
    id_mesa SERIAL PRIMARY KEY,
    numero INT NOT NULL UNIQUE CHECK (numero > 0),
    ubicacion VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'LIBRE' NOT NULL CHECK (estado IN ('LIBRE', 'OCUPADA', 'RESERVADA'))
);

CREATE TABLE IF NOT EXISTS productos (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    categoria VARCHAR(30) NOT NULL CHECK (categoria IN ('ENTRADA', 'FONDO', 'BEBIDA', 'POSTRE')),
    disponible BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_mesa INT NOT NULL REFERENCES mesas(id_mesa),
    id_usuario INT NOT NULL REFERENCES usuarios(id_usuario),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total NUMERIC(10,2) DEFAULT 0.00 NOT NULL CHECK (total >= 0),
    estado VARCHAR(20) DEFAULT 'PENDIENTE' NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ATENDIDO', 'PAGADO', 'CANCELADO'))
);

CREATE TABLE IF NOT EXISTS detalle_pedido (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    id_producto INT NOT NULL REFERENCES productos(id_producto),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    estado_preparacion VARCHAR(20) DEFAULT 'EN_ESPERA' NOT NULL CHECK (estado_preparacion IN ('EN_ESPERA', 'EN_PREPARACION', 'LISTO'))
);

CREATE TABLE IF NOT EXISTS pagos (
    id_pago SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL REFERENCES pedidos(id_pedido),
    tipo_pago VARCHAR(30) NOT NULL CHECK (tipo_pago IN ('EFECTIVO', 'TARJETA', 'BILLETERA_DIGITAL')),
    monto NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_pedidos_estado ON pedidos(estado);
CREATE INDEX IF NOT EXISTS idx_detalle_pedido_estado_prep ON detalle_pedido(estado_preparacion);
CREATE INDEX IF NOT EXISTS idx_detalle_pedido_pedido ON detalle_pedido(id_pedido);
