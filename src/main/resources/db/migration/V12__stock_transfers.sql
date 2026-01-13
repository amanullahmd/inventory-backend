-- V12: Stock Transfers Schema
-- Inter-warehouse stock transfers

CREATE TABLE stock_transfers (
    transfer_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE RESTRICT,
    batch_id BIGINT REFERENCES batches(batch_id) ON DELETE SET NULL,
    from_warehouse_id BIGINT NOT NULL REFERENCES warehouses(warehouse_id) ON DELETE RESTRICT,
    to_warehouse_id BIGINT NOT NULL REFERENCES warehouses(warehouse_id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    status VARCHAR(50) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'PARTIALLY_RECEIVED')),
    notes TEXT,
    created_by BIGINT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT different_warehouses CHECK (from_warehouse_id != to_warehouse_id)
);

CREATE INDEX idx_stock_transfers_from_warehouse ON stock_transfers(from_warehouse_id);
CREATE INDEX idx_stock_transfers_to_warehouse ON stock_transfers(to_warehouse_id);
CREATE INDEX idx_stock_transfers_item ON stock_transfers(item_id);
CREATE INDEX idx_stock_transfers_status ON stock_transfers(status);
