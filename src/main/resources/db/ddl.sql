-- =====================================================
-- DDL SQL para PostgreSQL - Sistema de Inventario
-- =====================================================

-- Eliminar tablas si existen (en orden inverso por dependencias)
DROP TABLE IF EXISTS inventory_movement_details CASCADE;
DROP TABLE IF EXISTS inventory_movements CASCADE;
DROP TABLE IF EXISTS product_stock CASCADE;
DROP TABLE IF EXISTS product_details CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS user_business CASCADE;
DROP TABLE IF EXISTS business CASCADE;
DROP TABLE IF EXISTS user_revoked_permissions CASCADE;
DROP TABLE IF EXISTS user_additional_permissions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS permission CASCADE;

-- =====================================================
-- TABLAS BASE (sin dependencias)
-- =====================================================

-- Tabla: permission
CREATE TABLE permission (
    code VARCHAR(255) PRIMARY KEY
);

-- Tabla: role
CREATE TABLE role (
    name VARCHAR(255) PRIMARY KEY
);

-- Tabla: role_permissions (relación Role <-> Permission)
CREATE TABLE role_permissions (
    role_name VARCHAR(255) NOT NULL,
    permission_code VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_name, permission_code),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_name) 
        REFERENCES role(name) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_code) 
        REFERENCES permission(code) ON DELETE CASCADE
);

-- =====================================================
-- TABLA: users
-- =====================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    enabled BOOLEAN DEFAULT FALSE,
    locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    owner_id BIGINT,
    CONSTRAINT fk_users_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

-- Índices para users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_owner_id ON users(owner_id);

-- =====================================================
-- TABLAS DE RELACIÓN: users <-> roles/permissions
-- =====================================================

-- Tabla: user_roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_name) 
        REFERENCES role(name) ON DELETE CASCADE
);

-- Tabla: user_additional_permissions
CREATE TABLE user_additional_permissions (
    user_id BIGINT NOT NULL,
    permission_code VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, permission_code),
    CONSTRAINT fk_user_add_perms_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_add_perms_permission FOREIGN KEY (permission_code) 
        REFERENCES permission(code) ON DELETE CASCADE
);

-- Tabla: user_revoked_permissions
CREATE TABLE user_revoked_permissions (
    user_id BIGINT NOT NULL,
    permission_code VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, permission_code),
    CONSTRAINT fk_user_rev_perms_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_rev_perms_permission FOREIGN KEY (permission_code) 
        REFERENCES permission(code) ON DELETE CASCADE
);

-- =====================================================
-- TABLA: business
-- =====================================================

CREATE TABLE business (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    owner_id BIGINT,
    CONSTRAINT fk_business_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_business_owner_id ON business(owner_id);

-- Tabla: user_business (relación ManyToMany)
CREATE TABLE user_business (
    user_id BIGINT NOT NULL,
    business_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, business_id),
    CONSTRAINT fk_user_business_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_business_business FOREIGN KEY (business_id) 
        REFERENCES business(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLA: warehouses
-- =====================================================

CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    owner_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_warehouses_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_warehouses_created_by FOREIGN KEY (created_by) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_warehouses_owner_id ON warehouses(owner_id);
CREATE INDEX idx_warehouses_created_by ON warehouses(created_by);

-- =====================================================
-- TABLA: category
-- =====================================================

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    owner_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_category_created_by FOREIGN KEY (created_by) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_category_owner_id ON category(owner_id);
CREATE INDEX idx_category_created_by ON category(created_by);

-- =====================================================
-- TABLA: product
-- =====================================================

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) UNIQUE,
    barcode VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    has_expiration_date BOOLEAN DEFAULT FALSE NOT NULL,
    category_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) 
        REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_created_by FOREIGN KEY (created_by) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_sku ON product(sku);
CREATE INDEX idx_product_barcode ON product(barcode);
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_owner_id ON product(owner_id);
CREATE INDEX idx_product_created_by ON product(created_by);

-- =====================================================
-- TABLA: product_details
-- =====================================================

CREATE TABLE product_details (
    id BIGSERIAL PRIMARY KEY,
    weight DOUBLE PRECISION,
    weight_unit VARCHAR(50),
    length DOUBLE PRECISION,
    width DOUBLE PRECISION,
    dimension_unit VARCHAR(50),
    product_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_details_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_details_product_id ON product_details(product_id);

-- =====================================================
-- TABLA: product_stock
-- =====================================================

CREATE TABLE product_stock (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 0,
    lot_number VARCHAR(255) NOT NULL,
    expiration_date DATE,
    unit_cost DOUBLE PRECISION NOT NULL,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_stock_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_stock_warehouse FOREIGN KEY (warehouse_id) 
        REFERENCES warehouses(id) ON DELETE CASCADE,
    CONSTRAINT uq_product_stock_lot UNIQUE (product_id, warehouse_id, lot_number)
);

CREATE INDEX idx_product_stock_product_id ON product_stock(product_id);
CREATE INDEX idx_product_stock_warehouse_id ON product_stock(warehouse_id);
CREATE INDEX idx_product_stock_lot_number ON product_stock(lot_number);

-- =====================================================
-- TABLA: inventory_movements
-- =====================================================

CREATE TABLE inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    movement_type VARCHAR(15) NOT NULL,
    reference_document VARCHAR(255),
    note TEXT,
    warehouse_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_movements_warehouse FOREIGN KEY (warehouse_id) 
        REFERENCES warehouses(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_movements_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_movements_created_by FOREIGN KEY (created_by) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_inv_movements_warehouse_id ON inventory_movements(warehouse_id);
CREATE INDEX idx_inv_movements_owner_id ON inventory_movements(owner_id);
CREATE INDEX idx_inv_movements_created_by ON inventory_movements(created_by);
CREATE INDEX idx_inv_movements_type ON inventory_movements(movement_type);
CREATE INDEX idx_inv_movements_created_at ON inventory_movements(created_at);

-- =====================================================
-- TABLA: inventory_movement_details
-- =====================================================

CREATE TABLE inventory_movement_details (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    unit_cost DOUBLE PRECISION,
    lot_number VARCHAR(255),
    sell_price_unit DOUBLE PRECISION,
    expiration_date DATE,
    movement_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_mov_details_movement FOREIGN KEY (movement_id) 
        REFERENCES inventory_movements(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_mov_details_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX idx_inv_mov_details_movement_id ON inventory_movement_details(movement_id);
CREATE INDEX idx_inv_mov_details_product_id ON inventory_movement_details(product_id);
CREATE INDEX idx_inv_mov_details_lot_number ON inventory_movement_details(lot_number);

-- =====================================================
-- COMENTARIOS EN LAS TABLAS
-- =====================================================

COMMENT ON TABLE users IS 'Tabla de usuarios del sistema';
COMMENT ON TABLE role IS 'Roles de usuario';
COMMENT ON TABLE permission IS 'Permisos del sistema';
COMMENT ON TABLE business IS 'Negocios asociados a usuarios';
COMMENT ON TABLE category IS 'Categorías de productos';
COMMENT ON TABLE product IS 'Productos del inventario';
COMMENT ON TABLE product_details IS 'Detalles físicos de productos';
COMMENT ON TABLE product_stock IS 'Stock de productos por almacén y lote';
COMMENT ON TABLE warehouses IS 'Almacenes';
COMMENT ON TABLE inventory_movements IS 'Movimientos de inventario (entradas/salidas)';
COMMENT ON TABLE inventory_movement_details IS 'Detalle de productos en cada movimiento';

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
