-- V12: Add Audit Triggers
-- Automatic audit logging for all tables

CREATE OR REPLACE FUNCTION audit_log_action()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_values, new_values, created_at)
  VALUES (
    COALESCE(current_setting('app.current_user_id')::BIGINT, 1),
    CASE 
      WHEN TG_OP = 'INSERT' THEN 'CREATE'
      WHEN TG_OP = 'UPDATE' THEN 'UPDATE'
      WHEN TG_OP = 'DELETE' THEN 'DELETE'
    END,
    TG_TABLE_NAME,
    COALESCE(NEW.item_id, NEW.user_id, NEW.category_id, NEW.supplier_id, NEW.warehouse_id, NEW.batch_id, NEW.purchase_order_id, NEW.sales_order_id, NEW.transfer_id, OLD.item_id, OLD.user_id, OLD.category_id, OLD.supplier_id, OLD.warehouse_id, OLD.batch_id, OLD.purchase_order_id, OLD.sales_order_id, OLD.transfer_id),
    CASE WHEN TG_OP = 'DELETE' OR TG_OP = 'UPDATE' THEN row_to_json(OLD) ELSE NULL END,
    CASE WHEN TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN row_to_json(NEW) ELSE NULL END,
    CURRENT_TIMESTAMP
  );
  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION audit_stock_movement()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, new_values, created_at)
  VALUES (
    NEW.performed_by,
    'STOCK_MOVEMENT',
    'inventory_transactions',
    NEW.transaction_id,
    jsonb_build_object(
      'item_id', NEW.item_id,
      'warehouse_id', NEW.warehouse_id,
      'quantity', NEW.quantity,
      'transaction_type', NEW.transaction_type
    ),
    CURRENT_TIMESTAMP
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_users_trigger
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_categories_trigger
AFTER INSERT OR UPDATE OR DELETE ON categories
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_items_trigger
AFTER INSERT OR UPDATE OR DELETE ON items
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_suppliers_trigger
AFTER INSERT OR UPDATE OR DELETE ON suppliers
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_warehouses_trigger
AFTER INSERT OR UPDATE OR DELETE ON warehouses
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_batches_trigger
AFTER INSERT OR UPDATE OR DELETE ON batches
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_inventory_transactions_trigger
AFTER INSERT ON inventory_transactions
FOR EACH ROW EXECUTE FUNCTION audit_stock_movement();

CREATE TRIGGER audit_purchase_orders_trigger
AFTER INSERT OR UPDATE OR DELETE ON purchase_orders
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_sales_orders_trigger
AFTER INSERT OR UPDATE OR DELETE ON sales_orders
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE TRIGGER audit_stock_transfers_trigger
AFTER INSERT OR UPDATE OR DELETE ON stock_transfers
FOR EACH ROW EXECUTE FUNCTION audit_log_action();

CREATE OR REPLACE FUNCTION refresh_stock_summary()
RETURNS TRIGGER AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY stock_summary;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER refresh_stock_summary_trigger
AFTER INSERT ON inventory_transactions
FOR EACH STATEMENT EXECUTE FUNCTION refresh_stock_summary();
