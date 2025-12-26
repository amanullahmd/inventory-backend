package management.backend.inventory.entity;

/**
 * Enumeration for audit log actions.
 * Requirements: 5.4 - Audit trail tracking for sensitive operations
 */
public enum AuditAction {
    /**
     * Entity creation action
     */
    CREATE,
    
    /**
     * Entity update action
     */
    UPDATE,
    
    /**
     * Entity deletion action
     */
    DELETE
}
