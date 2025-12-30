-- V13: Seed Initial Data
-- Sample data for development and testing

-- Ensure audit function is safe for all tables by using JSON extraction
CREATE OR REPLACE FUNCTION audit_log_action()
RETURNS TRIGGER AS $$
DECLARE
  v_entity_id BIGINT;
  v_old jsonb;
  v_new jsonb;
BEGIN
  v_new := CASE WHEN TG_OP IN ('INSERT','UPDATE') THEN to_jsonb(NEW) ELSE NULL END;
  v_old := CASE WHEN TG_OP IN ('DELETE','UPDATE') THEN to_jsonb(OLD) ELSE NULL END;

  v_entity_id := COALESCE(
    (v_new ->> 'item_id')::BIGINT,
    (v_new ->> 'user_id')::BIGINT,
    (v_new ->> 'category_id')::BIGINT,
    (v_new ->> 'supplier_id')::BIGINT,
    (v_new ->> 'warehouse_id')::BIGINT,
    (v_new ->> 'batch_id')::BIGINT,
    (v_new ->> 'purchase_order_id')::BIGINT,
    (v_new ->> 'sales_order_id')::BIGINT,
    (v_new ->> 'transfer_id')::BIGINT,
    (v_old ->> 'item_id')::BIGINT,
    (v_old ->> 'user_id')::BIGINT,
    (v_old ->> 'category_id')::BIGINT,
    (v_old ->> 'supplier_id')::BIGINT,
    (v_old ->> 'warehouse_id')::BIGINT,
    (v_old ->> 'batch_id')::BIGINT,
    (v_old ->> 'purchase_order_id')::BIGINT,
    (v_old ->> 'sales_order_id')::BIGINT,
    (v_old ->> 'transfer_id')::BIGINT
  );

  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_values, new_values, created_at)
  VALUES (
    COALESCE(current_setting('app.current_user_id', true)::BIGINT, 1),
    CASE 
      WHEN TG_OP = 'INSERT' THEN 'CREATE'
      WHEN TG_OP = 'UPDATE' THEN 'UPDATE'
      WHEN TG_OP = 'DELETE' THEN 'DELETE'
    END,
    TG_TABLE_NAME,
    v_entity_id,
    v_old,
    v_new,
    CURRENT_TIMESTAMP
  );

  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Temporarily disable audit triggers during initial seed
ALTER TABLE users DISABLE TRIGGER audit_users_trigger;
ALTER TABLE categories DISABLE TRIGGER audit_categories_trigger;
ALTER TABLE items DISABLE TRIGGER audit_items_trigger;
ALTER TABLE suppliers DISABLE TRIGGER audit_suppliers_trigger;
ALTER TABLE warehouses DISABLE TRIGGER audit_warehouses_trigger;
ALTER TABLE batches DISABLE TRIGGER audit_batches_trigger;
ALTER TABLE purchase_orders DISABLE TRIGGER audit_purchase_orders_trigger;
ALTER TABLE sales_orders DISABLE TRIGGER audit_sales_orders_trigger;
ALTER TABLE stock_transfers DISABLE TRIGGER audit_stock_transfers_trigger;

-- Insert users
INSERT INTO users (email, username, password, first_name, last_name, role, enabled, created_at, updated_at)
VALUES 
  ('admin@inventory.local', 'admin', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'Admin', 'User', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('user@inventory.local', 'user', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'Regular', 'User', 'USER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user profiles
INSERT INTO user_profiles (user_id, full_name, phone, address, branch_name, created_at, updated_at)
SELECT u.user_id, 'Administrator', '+1-555-0100', '123 Admin St', 'Main Branch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'admin@inventory.local';

INSERT INTO user_profiles (user_id, full_name, phone, address, branch_name, created_at, updated_at)
SELECT u.user_id, 'Regular User', '+1-555-0101', '456 User Ave', 'Main Branch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'user@inventory.local';

-- Insert categories
INSERT INTO categories (name, description, is_active, created_at, updated_at)
VALUES 
  ('Electronics', 'Electronic devices and components', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Clothing', 'Apparel and accessories', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Food & Beverage', 'Food and beverage products', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Office Supplies', 'Office equipment and supplies', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert suppliers
INSERT INTO suppliers (name, email, phone, address, contact_person, is_active, created_at, updated_at)
VALUES 
  ('Tech Supplies Inc', 'contact@techsupplies.com', '+1-555-1000', '789 Tech Blvd', 'John Smith', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Fashion Wholesale', 'sales@fashionwholesale.com', '+1-555-1001', '321 Fashion Way', 'Jane Doe', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Food Distributors Ltd', 'orders@fooddist.com', '+1-555-1002', '654 Food Park', 'Bob Johnson', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert warehouses
INSERT INTO warehouses (name, address, capacity_units, is_active, created_at, updated_at)
VALUES 
  ('Main Warehouse', '100 Warehouse St', 10000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Secondary Warehouse', '200 Storage Ave', 5000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Distribution Center', '300 Logistics Rd', 15000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert items
INSERT INTO items (category_id, name, sku, description, unit_price, current_stock, minimum_stock, maximum_stock, is_active, created_at, updated_at)
VALUES 
  (1, 'Laptop Computer', 'ELEC-001', 'High-performance laptop', 999.99, 50, 5, 50, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, 'USB Cable', 'ELEC-002', 'USB 3.0 cable', 9.99, 200, 50, 500, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'T-Shirt', 'CLTH-001', 'Cotton t-shirt', 19.99, 100, 20, 200, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'Jeans', 'CLTH-002', 'Denim jeans', 49.99, 75, 15, 150, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'Coffee Beans', 'FOOD-001', 'Premium coffee beans', 12.99, 30, 10, 100, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 'Notebook', 'OFFC-001', 'Spiral notebook', 5.99, 500, 100, 1000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 'Pen Set', 'OFFC-002', 'Ballpoint pen set', 8.99, 300, 50, 500, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert batches
INSERT INTO batches (item_id, batch_number, supplier_id, expiry_date, manufacturing_date, quantity_received, is_active, created_at)
VALUES 
  (1, 'BATCH-001', 1, '2026-12-31', '2024-01-01', 100, TRUE, CURRENT_TIMESTAMP),
  (2, 'BATCH-002', 1, '2027-06-30', '2024-06-01', 500, TRUE, CURRENT_TIMESTAMP),
  (3, 'BATCH-003', 2, '2025-12-31', '2024-12-01', 200, TRUE, CURRENT_TIMESTAMP),
  (5, 'BATCH-004', 3, '2025-03-31', '2024-09-01', 50, TRUE, CURRENT_TIMESTAMP);

-- Insert item prices
INSERT INTO item_prices (item_id, price_type, price, currency, effective_from, is_active, created_at)
VALUES 
  (1, 'RETAIL', 999.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (1, 'WHOLESALE', 799.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (2, 'RETAIL', 9.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (3, 'RETAIL', 19.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (4, 'RETAIL', 49.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (5, 'RETAIL', 12.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (6, 'RETAIL', 5.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP),
  (7, 'RETAIL', 8.99, 'EUR', CURRENT_DATE, TRUE, CURRENT_TIMESTAMP);

-- Skipped inventory_transactions seed rows for portability

-- Skipped purchase_orders seed rows for portability

-- Skipped purchase_order_items seed rows for portability

-- Skipped sales_orders seed rows for portability

-- Skipped sales_order_items seed rows for portability

-- Skipped stock_transfers seed rows for portability

-- Re-enable audit triggers after seeding
ALTER TABLE users ENABLE TRIGGER audit_users_trigger;
ALTER TABLE categories ENABLE TRIGGER audit_categories_trigger;
ALTER TABLE items ENABLE TRIGGER audit_items_trigger;
ALTER TABLE suppliers ENABLE TRIGGER audit_suppliers_trigger;
ALTER TABLE warehouses ENABLE TRIGGER audit_warehouses_trigger;
ALTER TABLE batches ENABLE TRIGGER audit_batches_trigger;
ALTER TABLE purchase_orders ENABLE TRIGGER audit_purchase_orders_trigger;
ALTER TABLE sales_orders ENABLE TRIGGER audit_sales_orders_trigger;
ALTER TABLE stock_transfers ENABLE TRIGGER audit_stock_transfers_trigger;
