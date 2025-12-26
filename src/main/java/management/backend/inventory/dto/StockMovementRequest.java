package management.backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for stock movement operations.
 * Requirements: 4.1, 8.4, 8.5, 2.1, 2.2 - Stock movement request for IN/OUT operations
 */
public class StockMovementRequest {
    
    @NotNull(message = "Item ID is required")
    private Long itemId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    private Long quantity;
    
    private String notes;
    
    private String referenceNumber;
    
    private String reason;
    
    private String recipient;
    
    private String reasonType;
    
    // Default constructor
    public StockMovementRequest() {}
    
    // Constructor
    public StockMovementRequest(Long itemId, Long quantity, String notes) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.notes = notes;
    }

    // Constructor with reference number
    public StockMovementRequest(Long itemId, Long quantity, String notes, String referenceNumber) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.notes = notes;
        this.referenceNumber = referenceNumber;
    }
    
    // Getters and setters
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public Long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getReasonType() {
        return reasonType;
    }
    
    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }
    
    @Override
    public String toString() {
        return "StockMovementRequest{" +
                "itemId=" + itemId +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", reason='" + reason + '\'' +
                ", recipient='" + recipient + '\'' +
                ", reasonType='" + reasonType + '\'' +
                '}';
    }
}