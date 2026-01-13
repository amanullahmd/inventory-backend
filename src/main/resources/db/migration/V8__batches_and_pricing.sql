-- V8: Batches and Item Pricing Schema
-- Batch/lot tracking and flexible pricing

CREATE TABLE batches (
    batch_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    batch_number VARCHAR(100) NOT NULL,
    supplier_id BIGINT REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    expiry_date DATE,
    manufacturing_date DATE,
    quantity_received INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_batch_per_item UNIQUE(item_id, batch_number),
    CONSTRAINT valid_dates CHECK (manufacturing_date IS NULL OR expiry_date IS NULL OR manufacturing_date <= expiry_date)
);

CREATE INDEX idx_batches_item_id ON batches(item_id);
CREATE INDEX idx_batches_expiry ON batches(expiry_date);
CREATE INDEX idx_batches_active ON batches(is_active);

CREATE TABLE item_prices (
    price_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    price_type VARCHAR(30) DEFAULT 'RETAIL',
    price NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    currency VARCHAR(10) DEFAULT 'EUR',
    effective_from DATE DEFAULT CURRENT_DATE,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_date_range CHECK (effective_from IS NULL OR effective_to IS NULL OR effective_from <= effective_to)
);

CREATE INDEX idx_item_prices_item_id ON item_prices(item_id);
CREATE INDEX idx_item_prices_active ON item_prices(is_active);
