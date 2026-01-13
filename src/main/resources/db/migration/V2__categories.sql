-- V2: Categories Schema
-- Product categorization

CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    color VARCHAR(7),
    icon VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_name ON categories(name);
CREATE INDEX idx_categories_is_active ON categories(is_active);
CREATE INDEX idx_categories_display_order ON categories(display_order);
CREATE UNIQUE INDEX ux_categories_code ON categories(category_code);

-- Insert default category for uncategorized items
INSERT INTO categories (name, description, color, is_active, display_order, category_code)
VALUES ('Uncategorized', 'Default category', '#9CA3AF', TRUE, 0, 'CAT-UNC');
