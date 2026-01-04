ALTER TABLE purchase_orders
  DROP CONSTRAINT IF EXISTS purchase_orders_status_check;

ALTER TABLE purchase_orders
  ADD CONSTRAINT purchase_orders_status_check
  CHECK (status IN ('DRAFT','APPROVED','PARTIAL_RECEIVED','RECEIVED','CLOSED','CANCELLED'));

UPDATE purchase_orders
SET status = UPPER(status);
