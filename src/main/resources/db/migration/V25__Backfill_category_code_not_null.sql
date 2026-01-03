UPDATE categories
SET category_code = COALESCE(category_code, 'CAT-' || category_id);

ALTER TABLE categories
  ALTER COLUMN category_code SET NOT NULL;

