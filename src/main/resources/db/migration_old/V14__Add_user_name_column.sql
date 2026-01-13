-- V14: Align users schema with JPA entity
-- Adds 'name' column to users and backfills from first_name/last_name when available

ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255);

UPDATE users
SET name = COALESCE(NULLIF(TRIM(CONCAT_WS(' ', first_name, last_name)), ''), name);

-- Optional: drop first_name/last_name if not used elsewhere
-- ALTER TABLE users DROP COLUMN IF EXISTS first_name;
-- ALTER TABLE users DROP COLUMN IF EXISTS last_name;
