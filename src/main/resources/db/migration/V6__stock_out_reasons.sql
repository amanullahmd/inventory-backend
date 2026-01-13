-- V6: Stock Out Reasons Schema
-- Reasons for stock outflows

CREATE TABLE stock_out_reasons (
    reason_id BIGSERIAL PRIMARY KEY,
    reason_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_out_reasons_active ON stock_out_reasons(is_active);

-- Insert default stock out reasons
INSERT INTO stock_out_reasons (reason_name, description, is_active) VALUES 
    ('Sales', 'Stock sold to customers', TRUE),
    ('Damage', 'Stock damaged or defective', TRUE),
    ('Loss', 'Stock lost or missing', TRUE),
    ('Expiry', 'Stock expired or obsolete', TRUE),
    ('Return', 'Stock returned by customer', TRUE),
    ('Transfer', 'Stock transferred to another warehouse', TRUE),
    ('Adjustment', 'Inventory adjustment', TRUE),
    ('Other', 'Other reason', TRUE);
