ALTER TABLE suppliers
  ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

UPDATE suppliers SET status = 'ACTIVE' WHERE is_active = TRUE;
UPDATE suppliers SET status = 'INACTIVE' WHERE is_active = FALSE;

CREATE INDEX IF NOT EXISTS idx_suppliers_status ON suppliers (status);

