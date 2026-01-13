-- V18: Audit Triggers
-- Automatic audit logging for all tables

-- Generic audit function
CREATE OR REPLACE FUNCTION audit_log_action()
RETURNS TRIGGER AS $$
DECLARE
  new_json jsonb := CASE WHEN TG_OP = 'DELETE' THEN NULL ELSE to_jsonb(NEW) END;
  old_json jsonb := CASE WHEN TG_OP = 'INSERT' THEN NULL ELSE to_jsonb(OLD) END;
  entity_id BIGINT;
  actor_id BIGINT := current_setting('app.current_user_id', true)::BIGINT;
BEGIN
  entity_id := COALESCE(
    (new_json ->> 'item_id')::BIGINT, (new_json ->> 'user_id')::BIGINT,
    (new_json ->> 'category_id')::BIGINT, (new_json ->> 'supplier_id')::BIGINT,
    (new_json ->> 'warehouse_id')::BIGINT, (new_json ->> 'batch_id')::BIGINT,
    (new_json ->> 'purchase_order_id')::BIGINT, (new_json ->> 'sales_order_id')::BIGINT,
    (new_json ->> 'transfer_id')::BIGINT, (old_json ->> 'item_id')::BIGINT,
    (old_json ->> 'user_id')::BIGINT, (old_json ->> 'category_id')::BIGINT,
    (old_json ->> 'supplier_id')::BIGINT, (old_json ->> 'warehouse_id')::BIGINT,
    (old_json ->> 'batch_id')::BIGINT, (old_json ->> 'purchase_order_id')::BIGINT,
    (old_json ->> 'sales_order_id')::BIGINT, (old_json ->> 'transfer_id')::BIGINT
  );

  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_values, new_values, created_at)
  VALUES (
    actor_id,
    CASE WHEN TG_OP = 'INSERT' THEN 'CREATE' WHEN TG_OP = 'UPDATE' THEN 'UPDATE' WHEN TG_OP = 'DELETE' THEN 'DELETE' END,
    TG_TABLE_NAME, entity_id,
    CASE WHEN TG_OP IN ('DELETE','UPDATE') THEN to_jsonb(OLD) ELSE NULL END,
    CASE WHEN TG_OP IN ('INSERT','UPDATE') THEN to_jsonb(NEW) ELSE NULL END,
    CURRENT_TIMESTAMP
  );
  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Stock movement audit function
CREATE OR REPLACE FUNCTION audit_stock_movement()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, new_values, created_at)
  VALUES (
    NEW.performed_by, 'STOCK_MOVEMENT', 'inventory_transactions', NEW.transaction_id,
    jsonb_build_object('item_id', NEW.item_id, 'warehouse_id', NEW.warehouse_id, 'quantity', NEW.quantity, 'transaction_type', NEW.transaction_type),
    CURRENT_TIMESTAMP
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Refresh stock summary function
CREATE OR REPLACE FUNCTION refresh_stock_summary()
RETURNS TRIGGER AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY stock_summary;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for all tables
CREATE TRIGGER audit_users_trigger AFTER INSERT OR UPDATE OR DELETE ON users FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_categories_trigger AFTER INSERT OR UPDATE OR DELETE ON categories FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_items_trigger AFTER INSERT OR UPDATE OR DELETE ON items FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_suppliers_trigger AFTER INSERT OR UPDATE OR DELETE ON suppliers FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_warehouses_trigger AFTER INSERT OR UPDATE OR DELETE ON warehouses FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_batches_trigger AFTER INSERT OR UPDATE OR DELETE ON batches FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_inventory_transactions_trigger AFTER INSERT ON inventory_transactions FOR EACH ROW EXECUTE FUNCTION audit_stock_movement();
CREATE TRIGGER audit_purchase_orders_trigger AFTER INSERT OR UPDATE OR DELETE ON purchase_orders FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_sales_orders_trigger AFTER INSERT OR UPDATE OR DELETE ON sales_orders FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER audit_stock_transfers_trigger AFTER INSERT OR UPDATE OR DELETE ON stock_transfers FOR EACH ROW EXECUTE FUNCTION audit_log_action();
CREATE TRIGGER refresh_stock_summary_trigger AFTER INSERT ON inventory_transactions FOR EACH STATEMENT EXECUTE FUNCTION refresh_stock_summary();
