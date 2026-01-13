-- V14: Demands Schema
-- Demand/requisition management

CREATE TABLE demands (
    demand_id BIGSERIAL PRIMARY KEY,
    demand_code VARCHAR(50),
    demander_name VARCHAR(255) NOT NULL,
    position VARCHAR(255),
    grade VARCHAR(100),
    item_id BIGINT NOT NULL REFERENCES items(item_id),
    unit VARCHAR(50),
    warehouse_id BIGINT REFERENCES warehouses(warehouse_id),
    employee_id BIGINT REFERENCES employees(employee_id),
    requested_by BIGINT NOT NULL REFERENCES users(user_id),
    status VARCHAR(50) DEFAULT 'DRAFT',
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_demands_code ON demands(demand_code) WHERE demand_code IS NOT NULL;
CREATE INDEX idx_demands_item ON demands(item_id);
CREATE INDEX idx_demands_requested_by ON demands(requested_by);
CREATE INDEX idx_demands_status ON demands(status);
CREATE INDEX idx_demands_employee ON demands(employee_id);

-- Demand items table
CREATE TABLE demand_items (
    demand_item_id BIGSERIAL PRIMARY KEY,
    demand_id BIGINT NOT NULL REFERENCES demands(demand_id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES items(item_id),
    units INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_demand_items_demand ON demand_items(demand_id);
CREATE INDEX idx_demand_items_item ON demand_items(item_id);
