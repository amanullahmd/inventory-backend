-- V15: Stock Outs Schema
-- Stock out tracking

CREATE TABLE stock_outs (
    id BIGSERIAL PRIMARY KEY,
    stock_out_type VARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items(item_id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    stock_out_date TIMESTAMP NOT NULL,
    note VARCHAR(500),
    branch_id BIGINT REFERENCES warehouses(warehouse_id),
    employee_id BIGINT REFERENCES employees(employee_id),
    source_warehouse_id BIGINT REFERENCES warehouses(warehouse_id),
    reference_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_out_item ON stock_outs(item_id);
CREATE INDEX idx_stock_out_type ON stock_outs(stock_out_type);
CREATE INDEX idx_stock_outs_source_warehouse ON stock_outs(source_warehouse_id);
