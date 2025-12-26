-- V15: Add Stock-Out Reasons Tracking
-- Adds reason and recipient tracking to stock movements for SaaS features

-- ============================================================================
-- ALTER STOCK_MOVEMENTS TABLE
-- ============================================================================

-- Add reason column for stock-out reason tracking
ALTER TABLE stock_movements ADD COLUMN reason VARCHAR(100);

-- Add recipient column for tracking who received the stock
ALTER TABLE stock_movements ADD COLUMN recipient VARCHAR(255);

-- Add reason_type column for predefined reason categorization
ALTER TABLE stock_movements ADD COLUMN reason_type VARCHAR(50);

-- ============================================================================
-- CREATE INDEXES FOR PERFORMANCE
-- ============================================================================

-- Index for filtering by reason type
CREATE INDEX idx_stock_movements_reason_type ON stock_movements(reason_type);

-- Index for filtering by recipient
CREATE INDEX idx_stock_movements_recipient ON stock_movements(recipient);

-- Composite index for reason breakdown queries
CREATE INDEX idx_stock_movements_reason_date ON stock_movements(reason_type, created_at);

-- ============================================================================
-- ADD CONSTRAINTS
-- ============================================================================

-- Add check constraint for valid reason types
ALTER TABLE stock_movements 
ADD CONSTRAINT chk_stock_movements_reason_type 
CHECK (reason_type IS NULL OR reason_type IN ('TRANSFERRED', 'GIVEN', 'EXPIRED', 'LOST', 'USED', 'DAMAGED', 'OTHER'));

-- ============================================================================
-- MIGRATION NOTES
-- ============================================================================
-- - reason: Optional field for stock-out reason (required for OUT movements)
-- - recipient: Optional field for tracking who received the stock
-- - reason_type: Enum value for predefined reasons
-- - Existing stock movements will have NULL values for these columns
-- - New stock movements will require reason for OUT type movements
