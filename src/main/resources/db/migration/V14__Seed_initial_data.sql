-- V14: Seed Initial Data
-- Insert demo users and categories for testing

-- ============================================================================
-- SEED USERS
-- ============================================================================
-- Admin user: admin@example.com / Admin@123456
-- User: user@example.com / User@123456
INSERT INTO users (email, username, password, first_name, last_name, role, enabled, password_change_required)
VALUES 
    ('admin@example.com', 'admin', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy990qm', 'Admin', 'User', 'ADMIN', true, false),
    ('user@example.com', 'user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRj275', 'Regular', 'User', 'USER', true, false);

-- ============================================================================
-- SEED CATEGORIES
-- ============================================================================
INSERT INTO categories (name, description, color, icon, is_active, display_order)
VALUES 
    ('Electronics', 'Electronic devices and components', '#3B82F6', 'laptop', true, 1),
    ('Peripherals', 'Computer peripherals and accessories', '#8B5CF6', 'mouse', true, 2),
    ('Monitors', 'Display monitors and screens', '#EC4899', 'monitor', true, 3),
    ('Audio', 'Audio equipment and microphones', '#F59E0B', 'headphones', true, 4),
    ('Accessories', 'General accessories and supplies', '#10B981', 'box', true, 5);

-- ============================================================================
-- SEED ITEMS
-- ============================================================================
INSERT INTO items (category_id, name, sku, description, unit_price, current_stock, minimum_stock, maximum_stock, reorder_level, is_active)
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Electronics'),
    'MacBook Pro 16"', 'MBP-16-001', 'Apple MacBook Pro 16 inch', 2499.99, 5, 1, 10, 2, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Electronics'),
    'Dell XPS 13', 'DXP-13-002', 'Dell XPS 13 Laptop', 1299.99, 8, 2, 15, 3, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Peripherals'),
    'Logitech MX Master 3', 'LGM-MX3-006', 'Logitech MX Master 3 Mouse', 99.99, 25, 5, 50, 10, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Peripherals'),
    'Mechanical Keyboard RGB', 'MKB-RGB-016', 'RGB Mechanical Keyboard', 149.99, 15, 3, 30, 5, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Monitors'),
    'LG UltraWide 34"', 'LG-UW34-012', 'LG 34 inch UltraWide Monitor', 799.99, 3, 1, 8, 2, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Monitors'),
    'Dell S3422DWG', 'DLS-34-013', 'Dell 34 inch Curved Monitor', 699.99, 4, 1, 10, 2, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Audio'),
    'Blue Yeti Microphone', 'BLU-YET-023', 'Blue Yeti USB Microphone', 99.99, 12, 2, 25, 5, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Audio'),
    'Shure SM7B', 'SHR-SM7B-025', 'Shure SM7B Professional Microphone', 399.00, 2, 1, 5, 1, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Accessories'),
    'USB-C Cable 2m', 'USB-C2M-009', 'USB-C Cable 2 meters', 12.99, 100, 20, 200, 50, true
UNION ALL
SELECT 
    (SELECT category_id FROM categories WHERE name = 'Accessories'),
    'HDMI 2.1 Cable', 'HDMI-21-010', 'HDMI 2.1 Cable', 19.99, 75, 15, 150, 30, true;
