-- V7: Stock Movements Schema
-- Track all stock in/out movements

CREATE TABLE stock_movements (
    stock_movement_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE RESTRICT,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    supplier_id BIGINT REFERENCES suppliers(supplier_id),
    warehouse_id BIGINT REFERENCES warehouses(warehouse_id),
    reason_id BIGINT REFERENCES stock_out_reasons(reason_id) ON DELETE SET NULL,
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('IN', 'OUT', 'ADJUSTMENT')),
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    reference_number VARCHAR(100),
    notes TEXT,
    reason VARCHAR(100),
    recipient VARCHAR(255),
    reason_type VARCHAR(50),
    source_mode VARCHAR(50),
    previous_stock BIGINT NOT NULL,
    new_stock BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_movements_item_id ON stock_movements(item_id);
CREATE INDEX idx_stock_movements_user_id ON stock_movements(user_id);
CREATE INDEX idx_stock_movements_movement_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(created_at);
CREATE INDEX idx_stock_movements_reference ON stock_movements(reference_number);
CREATE INDEX idx_stock_movements_supplier ON stock_movements(supplier_id);
CREATE INDEX idx_stock_movements_warehouse ON stock_movements(warehouse_id);
CREATE INDEX idx_stock_movements_source_mode ON stock_movements(source_mode);
