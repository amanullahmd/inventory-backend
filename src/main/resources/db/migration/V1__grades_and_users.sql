-- V1: Grades and Users Schema
-- Core authentication and user management tables

-- Grades table (for user classification)
CREATE TABLE grades (
    grade_id BIGSERIAL PRIMARY KEY,
    grade_number INTEGER NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Populate grades 1-20
INSERT INTO grades (grade_number, description) VALUES
(1, 'Grade 1 - Highest'), (2, 'Grade 2'), (3, 'Grade 3'), (4, 'Grade 4'), (5, 'Grade 5'),
(6, 'Grade 6'), (7, 'Grade 7'), (8, 'Grade 8'), (9, 'Grade 9'), (10, 'Grade 10'),
(11, 'Grade 11'), (12, 'Grade 12'), (13, 'Grade 13'), (14, 'Grade 14'), (15, 'Grade 15'),
(16, 'Grade 16'), (17, 'Grade 17'), (18, 'Grade 18'), (19, 'Grade 19'), (20, 'Grade 20 - Lowest');

-- Users table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    position VARCHAR(100),
    grade VARCHAR(50),
    grade_id BIGINT REFERENCES grades(grade_id),
    warehouse_id BIGINT,  -- FK added after warehouses table created
    enabled BOOLEAN NOT NULL DEFAULT true,
    password_change_required BOOLEAN NOT NULL DEFAULT false,
    temporary_password BOOLEAN NOT NULL DEFAULT false,
    last_password_change_at TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_users_warehouse_id ON users(warehouse_id);

-- User profiles table
CREATE TABLE user_profiles (
    profile_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    full_name VARCHAR(150),
    phone VARCHAR(30),
    address TEXT,
    branch_name VARCHAR(100),
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
