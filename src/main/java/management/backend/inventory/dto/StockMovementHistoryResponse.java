package management.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for stock movement history response.
 * Used for displaying stock movement history with all details including reasons.
 * 
 * Requirements: SaaS Features - Stock movement history display
 */
public class StockMovementHistoryResponse {
    
    private Long stockMovementId;
    private Long itemId;
    private String itemName;
    private String itemSku;
    private String movementType;
    private Long quantity;
    private Long previousStock;
    private Long newStock;
    private String reason;
    private String recipient;
    private String notes;
    private String referenceNumber;
    private String userName;
    private String userEmail;
    private LocalDateTime createdAt;
    private BigDecimal unitPrice;
    
    // Default constructor
    public StockMovementHistoryResponse() {}
    
    // Constructor with all fields
    public StockMovementHistoryResponse(
        Long stockMovementId,
        Long itemId,
        String itemName,
        String itemSku,
        String movementType,
        Long quantity,
        Long previousStock,
        Long newStock,
        String reason,
        String recipient,
        String notes,
        String referenceNumber,
        String userName,
        String userEmail,
        LocalDateTime createdAt,
        BigDecimal unitPrice
    ) {
        this.stockMovementId = stockMovementId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemSku = itemSku;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.recipient = recipient;
        this.notes = notes;
        this.referenceNumber = referenceNumber;
        this.userName = userName;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.unitPrice = unitPrice;
    }
    
    // Getters and setters
    public Long getStockMovementId() {
        return stockMovementId;
    }
    
    public void setStockMovementId(Long stockMovementId) {
        this.stockMovementId = stockMovementId;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemSku() {
        return itemSku;
    }
    
    public void setItemSku(String itemSku) {
        this.itemSku = itemSku;
    }
    
    public String getMovementType() {
        return movementType;
    }
    
    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
    
    public Long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public Long getPreviousStock() {
        return previousStock;
    }
    
    public void setPreviousStock(Long previousStock) {
        this.previousStock = previousStock;
    }
    
    public Long getNewStock() {
        return newStock;
    }
    
    public void setNewStock(Long newStock) {
        this.newStock = newStock;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    @Override
    public String toString() {
        return "StockMovementHistoryResponse{" +
                "stockMovementId=" + stockMovementId +
                ", itemName='" + itemName + '\'' +
                ", itemSku='" + itemSku + '\'' +
                ", movementType='" + movementType + '\'' +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                ", recipient='" + recipient + '\'' +
                ", userName='" + userName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
