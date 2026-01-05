ALTER TABLE stock_outs
ADD COLUMN branch_id BIGINT,
ADD COLUMN employee_id BIGINT,
ADD COLUMN reference_number VARCHAR(50);

ALTER TABLE stock_outs
ADD CONSTRAINT fk_stock_outs_branch
FOREIGN KEY (branch_id) REFERENCES warehouses (warehouse_id);

ALTER TABLE stock_outs
ADD CONSTRAINT fk_stock_outs_employee
FOREIGN KEY (employee_id) REFERENCES employees (employee_id);
