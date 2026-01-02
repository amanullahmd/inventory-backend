ALTER TABLE warehouses
  ADD COLUMN IF NOT EXISTS warehouse_code VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS ux_warehouses_code
  ON warehouses (warehouse_code)
  WHERE warehouse_code IS NOT NULL;

