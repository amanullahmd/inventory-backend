ALTER TABLE categories
  ADD COLUMN IF NOT EXISTS category_code VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_code
  ON categories (category_code)
  WHERE category_code IS NOT NULL;

