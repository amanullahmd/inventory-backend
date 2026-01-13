-- V3: Suppliers Schema
-- Supplier/vendor management

CREATE TABLE suppliers (
    supplier_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    registration_number VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(30),
    address TEXT,
    contact_person VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_active ON suppliers(is_active);
CREATE INDEX idx_suppliers_status ON suppliers(status);
CREATE UNIQUE INDEX ux_suppliers_registration_number ON suppliers(registration_number) WHERE registration_number IS NOT NULL;
