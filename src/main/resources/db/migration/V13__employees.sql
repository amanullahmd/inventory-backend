-- V13: Employees Schema
-- Employee management

CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(100) UNIQUE,
    name VARCHAR(200) NOT NULL,
    grade VARCHAR(100),
    position VARCHAR(150),
    branch_id BIGINT REFERENCES warehouses(warehouse_id),
    mobile_number VARCHAR(50),
    email VARCHAR(200),
    address TEXT,
    service_period VARCHAR(100),
    nid_number VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20),
    nationality VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_employees_name ON employees(name);
CREATE INDEX idx_employees_branch ON employees(branch_id);
