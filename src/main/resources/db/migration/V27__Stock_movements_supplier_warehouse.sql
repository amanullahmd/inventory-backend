ALTER TABLE stock_movements
  ADD COLUMN IF NOT EXISTS supplier_id BIGINT,
  ADD COLUMN IF NOT EXISTS warehouse_id BIGINT;

-- Add FKs if tables exist
ALTER TABLE stock_movements
  ADD CONSTRAINT fk_stock_movements_supplier
  FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id);

ALTER TABLE stock_movements
  ADD CONSTRAINT fk_stock_movements_warehouse
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id);

CREATE INDEX IF NOT EXISTS idx_stock_movements_supplier ON stock_movements(supplier_id);
CREATE INDEX IF NOT EXISTS idx_stock_movements_warehouse ON stock_movements(warehouse_id);
