-- V2: Add Stock Out Reasons Table
-- Tracks reasons for stock outflows

CREATE TABLE stock_out_reasons (
    reason_id BIGSERIAL PRIMARY KEY,
    reason_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_out_reasons_active ON stock_out_reasons(is_active);

-- Add reason_id column to stock_movements
ALTER TABLE stock_movements ADD COLUMN reason_id BIGINT REFERENCES stock_out_reasons(reason_id) ON DELETE SET NULL;

-- Insert default stock out reasons
INSERT INTO stock_out_reasons (reason_name, description, is_active, created_at, updated_at)
VALUES 
    ('Sales', 'Stock sold to customers', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Damage', 'Stock damaged or defective', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Loss', 'Stock lost or missing', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Expiry', 'Stock expired or obsolete', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Return', 'Stock returned by customer', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Transfer', 'Stock transferred to another warehouse', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Adjustment', 'Inventory adjustment', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Other', 'Other reason', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
