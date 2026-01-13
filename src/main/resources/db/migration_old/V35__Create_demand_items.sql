CREATE TABLE IF NOT EXISTS demand_items (
  demand_item_id BIGSERIAL PRIMARY KEY,
  demand_id BIGINT NOT NULL REFERENCES demands(demand_id) ON DELETE CASCADE,
  item_id BIGINT NOT NULL REFERENCES items(item_id),
  quantity INTEGER NOT NULL DEFAULT 1,
  unit VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_demand_items_demand ON demand_items(demand_id);
CREATE INDEX IF NOT EXISTS idx_demand_items_item ON demand_items(item_id);
