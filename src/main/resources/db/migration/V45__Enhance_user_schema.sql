-- V45: Enhance users table with first/last name and warehouse
-- Ensures first_name and last_name exist (in case they were dropped or unused)
-- Adds warehouse_id foreign key

ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);

-- If 'name' column exists and has data, try to split it into first/last name if they are null
-- This is a best-effort migration for existing data
UPDATE users 
SET 
    first_name = SPLIT_PART(name, ' ', 1),
    last_name = SUBSTRING(name FROM LENGTH(SPLIT_PART(name, ' ', 1)) + 2)
WHERE name IS NOT NULL AND (first_name IS NULL OR last_name IS NULL);

-- Add warehouse relationship (Branch)
ALTER TABLE users ADD COLUMN IF NOT EXISTS warehouse_id BIGINT;

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_users_warehouse') THEN
        ALTER TABLE users ADD CONSTRAINT fk_users_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id);
    END IF;
END $$;

-- Create index for warehouse lookups
CREATE INDEX IF NOT EXISTS idx_users_warehouse_id ON users(warehouse_id);
