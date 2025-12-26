package management.backend.inventory.entity;

/**
 * Enumeration for predefined stock-out reasons.
 * Used to categorize and track why inventory is being removed from stock.
 * 
 * Requirements: SaaS Features - Stock-out reasons tracking
 */
public enum StockOutReasonEnum {
    TRANSFERRED("Transferred to branch", "Stock transferred to another branch or location"),
    GIVEN("Given to person", "Stock given to a specific person"),
    EXPIRED("Expired", "Stock expired and removed from inventory"),
    LOST("Lost", "Stock lost or missing"),
    USED("Used", "Stock used for internal purposes"),
    DAMAGED("Damaged", "Stock damaged and unusable"),
    OTHER("Other", "Other reason not listed above");
    
    private final String label;
    private final String description;
    
    StockOutReasonEnum(String label, String description) {
        this.label = label;
        this.description = description;
    }
    
    /**
     * Get the display label for this reason.
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the description for this reason.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get reason by label (case-insensitive).
     * @param label the label to search for
     * @return the matching reason or null if not found
     */
    public static StockOutReasonEnum fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (StockOutReasonEnum reason : values()) {
            if (reason.label.equalsIgnoreCase(label)) {
                return reason;
            }
        }
        return null;
    }
    
    /**
     * Check if a reason type is valid.
     * @param reasonType the reason type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String reasonType) {
        if (reasonType == null) {
            return false;
        }
        try {
            StockOutReasonEnum.valueOf(reasonType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
