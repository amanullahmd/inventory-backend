-- V6: Add Batches and Item Pricing Tables
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
    CONSTRAINT valid_dates CHECK (manufacturing_date <= expiry_date)
);

CREATE INDEX idx_batches_item_id ON batches(item_id);
CREATE INDEX idx_batches_expiry ON batches(expiry_date);
CREATE INDEX idx_batches_active ON batches(is_active);

CREATE TABLE item_prices (
    price_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    price_type VARCHAR(30) DEFAULT 'RETAIL',
    price NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'EUR',
    effective_from DATE DEFAULT CURRENT_DATE,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT valid_price CHECK (price > 0),
    CONSTRAINT valid_date_range CHECK (effective_from <= effective_to OR effective_to IS NULL)
);

CREATE INDEX idx_item_prices_item_id ON item_prices(item_id);
CREATE INDEX idx_item_prices_active ON item_prices(is_active);
