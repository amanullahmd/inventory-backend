-- Migration: Consolidate first_name and last_name into single name column
-- This migration combines first_name and last_name into a single name column

-- Step 1: Add the new 'name' column
ALTER TABLE users ADD COLUMN name VARCHAR(255) NULL;

-- Step 2: Migrate data from first_name and last_name to name
UPDATE users 
SET name = CONCAT(
    COALESCE(first_name, ''),
    CASE 
        WHEN first_name IS NOT NULL AND last_name IS NOT NULL THEN ' '
        ELSE ''
    END,
    COALESCE(last_name, '')
)
WHERE first_name IS NOT NULL OR last_name IS NOT NULL;

-- Step 3: Drop the old columns
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;
