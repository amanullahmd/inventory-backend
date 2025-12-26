package management.backend.inventory.entity;

/**
 * Enumeration for stock movement types.
 * Requirements: 4.1, 6.3 - Movement type constraints for IN/OUT/ADJUSTMENT operations
 */
public enum MovementType {
    /**
     * Stock-in movement - adding items to inventory
     */
    IN,
    
    /**
     * Stock-out movement - removing items from inventory
     */
    OUT,
    
    /**
     * Stock adjustment - correcting inventory discrepancies
     */
    ADJUSTMENT
}