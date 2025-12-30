-- V11: Add Materialized Views for Performance
-- Pre-calculated views for common queries

CREATE MATERIALIZED VIEW stock_summary AS
SELECT
  item_id,
  warehouse_id,
  batch_id,
  SUM(CASE 
    WHEN transaction_type IN ('STOCK_IN', 'TRANSFER_IN') THEN quantity
    ELSE -quantity
  END) AS available_stock,
  MAX(created_at) AS last_updated
FROM inventory_transactions
GROUP BY item_id, warehouse_id, batch_id;

CREATE INDEX idx_stock_summary_item_warehouse ON stock_summary(item_id, warehouse_id);

CREATE VIEW stock_status AS
SELECT 
  i.item_id,
  i.name,
  i.sku,
  w.name as warehouse_name,
  COALESCE(ss.available_stock, 0) as available_stock,
  i.minimum_stock,
  i.maximum_stock,
  CASE 
    WHEN COALESCE(ss.available_stock, 0) < i.minimum_stock THEN 'LOW'
    WHEN COALESCE(ss.available_stock, 0) > i.maximum_stock THEN 'EXCESS'
    ELSE 'OK'
  END as stock_status,
  ss.last_updated
FROM items i
CROSS JOIN warehouses w
LEFT JOIN stock_summary ss ON i.item_id = ss.item_id AND w.warehouse_id = ss.warehouse_id
WHERE i.is_active = TRUE AND w.is_active = TRUE;

CREATE VIEW purchase_order_summary AS
SELECT 
  po.purchase_order_id,
  po.supplier_id,
  s.name as supplier_name,
  po.warehouse_id,
  w.name as warehouse_name,
  po.status,
  po.order_date,
  po.expected_delivery_date,
  COUNT(poi.purchase_order_item_id) as item_count,
  SUM(poi.quantity) as total_quantity,
  SUM(poi.quantity - COALESCE(poi.received_quantity, 0)) as pending_quantity,
  po.total_amount,
  po.created_by,
  po.created_at
FROM purchase_orders po
LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id
LEFT JOIN warehouses w ON po.warehouse_id = w.warehouse_id
LEFT JOIN purchase_order_items poi ON po.purchase_order_id = poi.purchase_order_id
GROUP BY po.purchase_order_id, s.supplier_id, s.name, w.warehouse_id, w.name;

CREATE VIEW sales_order_summary AS
SELECT 
  so.sales_order_id,
  so.warehouse_id,
  w.name as warehouse_name,
  so.status,
  so.order_date,
  so.delivery_date,
  so.customer_name,
  so.customer_email,
  COUNT(soi.sales_order_item_id) as item_count,
  SUM(soi.quantity) as total_quantity,
  so.total_amount,
  so.created_by,
  so.created_at
FROM sales_orders so
LEFT JOIN warehouses w ON so.warehouse_id = w.warehouse_id
LEFT JOIN sales_order_items soi ON so.sales_order_id = soi.sales_order_id
GROUP BY so.sales_order_id, w.warehouse_id, w.name;

CREATE VIEW inventory_transaction_summary AS
SELECT 
  i.item_id,
  i.name as item_name,
  i.sku,
  w.warehouse_id,
  w.name as warehouse_name,
  it.transaction_type,
  COUNT(*) as transaction_count,
  SUM(it.quantity) as total_quantity,
  MAX(it.created_at) as last_transaction_date
FROM inventory_transactions it
JOIN items i ON it.item_id = i.item_id
JOIN warehouses w ON it.warehouse_id = w.warehouse_id
GROUP BY i.item_id, i.name, i.sku, w.warehouse_id, w.name, it.transaction_type;

CREATE VIEW user_activity AS
SELECT 
  u.user_id,
  u.username,
  u.email,
  u.role,
  COUNT(DISTINCT al.audit_log_id) as total_actions,
  MAX(al.created_at) as last_action_date
FROM users u
LEFT JOIN audit_logs al ON u.user_id = al.user_id
GROUP BY u.user_id, u.username, u.email, u.role;
