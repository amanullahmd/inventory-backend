-- V18: Align stock_movements table with JPA entity
-- Add columns: reason, recipient, reason_type

ALTER TABLE stock_movements
  ADD COLUMN IF NOT EXISTS reason VARCHAR(100),
  ADD COLUMN IF NOT EXISTS recipient VARCHAR(255),
  ADD COLUMN IF NOT EXISTS reason_type VARCHAR(50);

