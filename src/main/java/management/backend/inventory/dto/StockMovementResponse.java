package management.backend.inventory.dto;

import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;

import java.time.LocalDateTime;

public class StockMovementResponse {
    
    private Long stockMovementId;
    private Long itemId;
    private String itemName;
    private String itemSku;
    private MovementType movementType;
    private Long quantity;
    private String referenceNumber;
    private String notes;
    private Long previousStock;
    private Long newStock;
    private LocalDateTime createdAt;
    
    // Default constructor
    public StockMovementResponse() {}
    
    // Constructor
    public StockMovementResponse(StockMovement movement) {
        this.stockMovementId = movement.getStockMovementId();
        this.itemId = movement.getItem().getItemId();
        this.itemName = movement.getItem().getName();
        this.itemSku = movement.getItem().getSku();
        this.movementType = movement.getMovementType();
        this.quantity = movement.getQuantity();
        this.referenceNumber = movement.getReferenceNumber();
        this.notes = movement.getNotes();
        this.previousStock = movement.getPreviousStock();
        this.newStock = movement.getNewStock();
        this.createdAt = movement.getCreatedAt();
    }
    
    // Getters and Setters
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
    
    public MovementType getMovementType() {
        return movementType;
    }
    
    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
    
    public Long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "StockMovementResponse{" +
                "stockMovementId=" + stockMovementId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemSku='" + itemSku + '\'' +
                ", movementType=" + movementType +
                ", quantity=" + quantity +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", notes='" + notes + '\'' +
                ", previousStock=" + previousStock +
                ", newStock=" + newStock +
                ", createdAt=" + createdAt +
                '}';
    }
}
