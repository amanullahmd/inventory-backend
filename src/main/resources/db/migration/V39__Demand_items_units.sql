ALTER TABLE demand_items
  ADD COLUMN IF NOT EXISTS units INTEGER NOT NULL DEFAULT 1;

UPDATE demand_items SET units = COALESCE(quantity, 1);

ALTER TABLE demand_items
  DROP COLUMN IF EXISTS quantity;

ALTER TABLE demand_items
  DROP COLUMN IF EXISTS unit;
