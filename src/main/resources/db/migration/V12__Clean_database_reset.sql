-- V12: Clean Database Reset
-- Drop all existing tables and start fresh with professional schema

-- Drop all existing tables
DROP TABLE IF EXISTS stock_movements CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop all sequences
DROP SEQUENCE IF EXISTS categories_category_id_seq;
DROP SEQUENCE IF EXISTS items_item_id_seq;
DROP SEQUENCE IF EXISTS stock_movements_stock_movement_id_seq;
DROP SEQUENCE IF EXISTS users_user_id_seq;
