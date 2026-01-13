-- V5: Items Schema
-- Product/inventory items

CREATE TABLE items (
    item_id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES categories(category_id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    unit_price NUMERIC(12, 2) CHECK (unit_price IS NULL OR unit_price >= 0),
    current_stock BIGINT NOT NULL DEFAULT 0 CHECK (current_stock >= 0),
    minimum_stock BIGINT NOT NULL DEFAULT 0 CHECK (minimum_stock >= 0),
    maximum_stock BIGINT,
    reorder_level BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_items_sku ON items(sku);
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_is_active ON items(is_active);
CREATE INDEX idx_items_current_stock ON items(current_stock);
