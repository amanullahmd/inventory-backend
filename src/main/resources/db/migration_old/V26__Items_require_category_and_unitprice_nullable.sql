-- Ensure a default category exists for backfilling
INSERT INTO categories (name, description, color, is_active, display_order, created_at, updated_at, category_code)
SELECT 'Uncategorized', 'Default category', '#9CA3AF', TRUE, 0, NOW(), NOW(), 'CAT-UNC'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Uncategorized');

-- Backfill items with NULL category_id to the default category
UPDATE items
SET category_id = (SELECT category_id FROM categories WHERE name = 'Uncategorized')
WHERE category_id IS NULL;

-- Make category mandatory
ALTER TABLE items
  ALTER COLUMN category_id SET NOT NULL;

-- Allow unit_price to be NULL (managed via stock-in flow)
ALTER TABLE items
  ALTER COLUMN unit_price DROP NOT NULL;

