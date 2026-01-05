ALTER TABLE stock_outs
ADD COLUMN source_warehouse_id BIGINT;

ALTER TABLE stock_outs
ADD CONSTRAINT fk_stock_outs_source_warehouse
FOREIGN KEY (source_warehouse_id) REFERENCES warehouses (warehouse_id);

CREATE INDEX idx_stock_outs_source_warehouse ON stock_outs(source_warehouse_id);
