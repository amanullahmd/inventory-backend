CREATE TABLE IF NOT EXISTS demands (
  demand_id BIGSERIAL PRIMARY KEY,
  demand_code VARCHAR(50),
  demander_name VARCHAR(255) NOT NULL,
  position VARCHAR(255),
  grade VARCHAR(100),
  item_id BIGINT NOT NULL REFERENCES items(item_id),
  unit VARCHAR(50),
  warehouse_id BIGINT REFERENCES warehouses(warehouse_id),
  requested_by BIGINT NOT NULL REFERENCES users(user_id),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_demands_code ON demands(demand_code) WHERE demand_code IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_demands_item ON demands(item_id);
CREATE INDEX IF NOT EXISTS idx_demands_requested_by ON demands(requested_by);
