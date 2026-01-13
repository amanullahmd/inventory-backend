ALTER TABLE stock_movements
  ADD COLUMN IF NOT EXISTS source_mode VARCHAR(50);

UPDATE stock_movements
SET source_mode = CASE WHEN supplier_id IS NOT NULL THEN 'SUPPLIER' ELSE 'NON_SUPPLIER' END
WHERE source_mode IS NULL;

CREATE INDEX IF NOT EXISTS idx_stock_movements_source_mode ON stock_movements(source_mode);
