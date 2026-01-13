-- V16: Make current_setting usage safe in audit trigger
-- Use current_setting('app.current_user_id', true) to avoid errors when not set

CREATE OR REPLACE FUNCTION audit_log_action()
RETURNS TRIGGER AS $$
DECLARE
  new_json jsonb := CASE WHEN TG_OP = 'DELETE' THEN NULL ELSE to_jsonb(NEW) END;
  old_json jsonb := CASE WHEN TG_OP = 'INSERT' THEN NULL ELSE to_jsonb(OLD) END;
  entity_id BIGINT;
  actor_id BIGINT := COALESCE(current_setting('app.current_user_id', true)::BIGINT, 1);
BEGIN
  entity_id := COALESCE(
    (new_json ->> 'item_id')::BIGINT,
    (new_json ->> 'user_id')::BIGINT,
    (new_json ->> 'category_id')::BIGINT,
    (new_json ->> 'supplier_id')::BIGINT,
    (new_json ->> 'warehouse_id')::BIGINT,
    (new_json ->> 'batch_id')::BIGINT,
    (new_json ->> 'purchase_order_id')::BIGINT,
    (new_json ->> 'sales_order_id')::BIGINT,
    (new_json ->> 'transfer_id')::BIGINT,
    (old_json ->> 'item_id')::BIGINT,
    (old_json ->> 'user_id')::BIGINT,
    (old_json ->> 'category_id')::BIGINT,
    (old_json ->> 'supplier_id')::BIGINT,
    (old_json ->> 'warehouse_id')::BIGINT,
    (old_json ->> 'batch_id')::BIGINT,
    (old_json ->> 'purchase_order_id')::BIGINT,
    (old_json ->> 'sales_order_id')::BIGINT,
    (old_json ->> 'transfer_id')::BIGINT
  );

  INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_values, new_values, created_at)
  VALUES (
    actor_id,
    CASE 
      WHEN TG_OP = 'INSERT' THEN 'CREATE'
      WHEN TG_OP = 'UPDATE' THEN 'UPDATE'
      WHEN TG_OP = 'DELETE' THEN 'DELETE'
    END,
    TG_TABLE_NAME,
    entity_id,
    CASE WHEN TG_OP IN ('DELETE','UPDATE') THEN to_jsonb(OLD) ELSE NULL END,
    CASE WHEN TG_OP IN ('INSERT','UPDATE') THEN to_jsonb(NEW) ELSE NULL END,
    CURRENT_TIMESTAMP
  );

  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

