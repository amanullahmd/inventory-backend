ALTER TABLE purchase_orders
  ADD COLUMN IF NOT EXISTS purchase_order_code VARCHAR(50);

UPDATE purchase_orders
SET purchase_order_code = 'PO-' || to_char(order_date, 'YYYYMMDD') || '-' || purchase_order_id
WHERE purchase_order_code IS NULL;

ALTER TABLE purchase_orders
  ALTER COLUMN purchase_order_code SET NOT NULL;

ALTER TABLE purchase_orders
  ADD CONSTRAINT uq_purchase_order_code UNIQUE (purchase_order_code);

CREATE INDEX IF NOT EXISTS idx_purchase_orders_code ON purchase_orders(purchase_order_code);
