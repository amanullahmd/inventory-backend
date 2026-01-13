ALTER TABLE demands
  ADD COLUMN IF NOT EXISTS status VARCHAR(50);

UPDATE demands SET status = COALESCE(status, 'DRAFT');

CREATE INDEX IF NOT EXISTS idx_demands_status ON demands(status);
