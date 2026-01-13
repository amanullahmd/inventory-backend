-- V4: Warehouses Schema
-- Warehouse/branch locations

CREATE TABLE warehouses (
    warehouse_id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    address TEXT,
    capacity_units INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_warehouses_name ON warehouses(name);
CREATE INDEX idx_warehouses_active ON warehouses(is_active);
CREATE INDEX idx_warehouses_status ON warehouses(status);
CREATE UNIQUE INDEX ux_warehouses_code ON warehouses(warehouse_code) WHERE warehouse_code IS NOT NULL;

-- Add FK from users to warehouses (deferred from V1)
ALTER TABLE users ADD CONSTRAINT fk_users_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id);
