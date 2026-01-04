ALTER TABLE demands
  ADD COLUMN IF NOT EXISTS employee_id BIGINT REFERENCES employees(employee_id);

CREATE INDEX IF NOT EXISTS idx_demands_employee ON demands(employee_id);
