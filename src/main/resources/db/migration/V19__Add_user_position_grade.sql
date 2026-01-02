-- V19: Add position and grade columns to users
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS position VARCHAR(100),
  ADD COLUMN IF NOT EXISTS grade VARCHAR(50);

