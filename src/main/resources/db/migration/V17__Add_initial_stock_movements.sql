-- Add initial stock movements for dummy items
-- This migration creates stock-in movements for all existing items to populate their inventory

-- First, get or create a system user for initial stock movements
-- We'll use the first admin user if it exists, otherwise create a system user

-- Insert stock movements for each item
-- The quantities match the expected stock levels from the dummy data

INSERT INTO stock_movements (item_id, user_id, movement_type, quantity, previous_stock, new_stock, reason, created_at)
SELECT 
    i.item_id,
    (SELECT user_id FROM users WHERE role = 'ADMIN' LIMIT 1),
    'IN',
    CASE 
        WHEN i.sku = 'MBP-16-001' THEN 12
        WHEN i.sku = 'DXP-13-002' THEN 28
        WHEN i.sku = 'HPP-15-003' THEN 45
        WHEN i.sku = 'LTP-X1-004' THEN 18
        WHEN i.sku = 'ASV-15-005' THEN 52
        WHEN i.sku = 'LGM-MX3-006' THEN 156
        WHEN i.sku = 'RZR-DA3-007' THEN 89
        WHEN i.sku = 'STS-R600-008' THEN 42
        WHEN i.sku = 'USB-C2M-009' THEN 234
        WHEN i.sku = 'HDMI-21-010' THEN 178
        WHEN i.sku = 'DP-14-011' THEN 95
        WHEN i.sku = 'LG-UW34-012' THEN 8
        WHEN i.sku = 'DLS-34-013' THEN 5
        WHEN i.sku = 'ASP-PA27-014' THEN 0
        WHEN i.sku = 'BNQ-EW27-015' THEN 12
        WHEN i.sku = 'LGW-4K-020' THEN 34
        WHEN i.sku = 'RZR-KP-021' THEN 19
        WHEN i.sku = 'ELG-FC-022' THEN 26
        WHEN i.sku = 'BLU-YET-023' THEN 112
        WHEN i.sku = 'ATA-2020-024' THEN 45
        WHEN i.sku = 'SHR-SM7B-025' THEN 8
        WHEN i.sku = 'DLM-RGB-026' THEN 89
        WHEN i.sku = 'MNA-DUL-027' THEN 56
        WHEN i.sku = 'LPS-ALU-028' THEN 134
        WHEN i.sku = 'PHS-PRM-029' THEN 267
        WHEN i.sku = 'DSP-XL-030' THEN 178
        WHEN i.sku = 'MKB-RGB-016' THEN 67
        WHEN i.sku = 'KCH-K8P-017' THEN 43
        WHEN i.sku = 'COR-K95-018' THEN 21
        WHEN i.sku = 'LGM-MXK-019' THEN 78
        ELSE 0
    END,
    0,
    CASE 
        WHEN i.sku = 'MBP-16-001' THEN 12
        WHEN i.sku = 'DXP-13-002' THEN 28
        WHEN i.sku = 'HPP-15-003' THEN 45
        WHEN i.sku = 'LTP-X1-004' THEN 18
        WHEN i.sku = 'ASV-15-005' THEN 52
        WHEN i.sku = 'LGM-MX3-006' THEN 156
        WHEN i.sku = 'RZR-DA3-007' THEN 89
        WHEN i.sku = 'STS-R600-008' THEN 42
        WHEN i.sku = 'USB-C2M-009' THEN 234
        WHEN i.sku = 'HDMI-21-010' THEN 178
        WHEN i.sku = 'DP-14-011' THEN 95
        WHEN i.sku = 'LG-UW34-012' THEN 8
        WHEN i.sku = 'DLS-34-013' THEN 5
        WHEN i.sku = 'ASP-PA27-014' THEN 0
        WHEN i.sku = 'BNQ-EW27-015' THEN 12
        WHEN i.sku = 'LGW-4K-020' THEN 34
        WHEN i.sku = 'RZR-KP-021' THEN 19
        WHEN i.sku = 'ELG-FC-022' THEN 26
        WHEN i.sku = 'BLU-YET-023' THEN 112
        WHEN i.sku = 'ATA-2020-024' THEN 45
        WHEN i.sku = 'SHR-SM7B-025' THEN 8
        WHEN i.sku = 'DLM-RGB-026' THEN 89
        WHEN i.sku = 'MNA-DUL-027' THEN 56
        WHEN i.sku = 'LPS-ALU-028' THEN 134
        WHEN i.sku = 'PHS-PRM-029' THEN 267
        WHEN i.sku = 'DSP-XL-030' THEN 178
        WHEN i.sku = 'MKB-RGB-016' THEN 67
        WHEN i.sku = 'KCH-K8P-017' THEN 43
        WHEN i.sku = 'COR-K95-018' THEN 21
        WHEN i.sku = 'LGM-MXK-019' THEN 78
        ELSE 0
    END,
    'Initial Stock',
    NOW()
FROM items i
WHERE NOT EXISTS (
    SELECT 1 FROM stock_movements sm WHERE sm.item_id = i.item_id
)
AND i.sku IN (
    'MBP-16-001', 'DXP-13-002', 'HPP-15-003', 'LTP-X1-004', 'ASV-15-005',
    'LGM-MX3-006', 'RZR-DA3-007', 'STS-R600-008', 'USB-C2M-009', 'HDMI-21-010',
    'DP-14-011', 'LG-UW34-012', 'DLS-34-013', 'ASP-PA27-014', 'BNQ-EW27-015',
    'LGW-4K-020', 'RZR-KP-021', 'ELG-FC-022', 'BLU-YET-023', 'ATA-2020-024',
    'SHR-SM7B-025', 'DLM-RGB-026', 'MNA-DUL-027', 'LPS-ALU-028', 'PHS-PRM-029',
    'DSP-XL-030', 'MKB-RGB-016', 'KCH-K8P-017', 'COR-K95-018', 'LGM-MXK-019'
);
