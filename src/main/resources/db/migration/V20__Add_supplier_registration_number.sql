ALTER TABLE suppliers
  ADD COLUMN IF NOT EXISTS registration_number VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS ux_suppliers_registration_number
  ON suppliers (registration_number)
  WHERE registration_number IS NOT NULL;

