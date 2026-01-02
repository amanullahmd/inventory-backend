ALTER TABLE warehouses
  ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

UPDATE warehouses SET status = 'ACTIVE' WHERE is_active = TRUE;
UPDATE warehouses SET status = 'INACTIVE' WHERE is_active = FALSE;

CREATE INDEX IF NOT EXISTS idx_warehouses_status ON warehouses (status);

