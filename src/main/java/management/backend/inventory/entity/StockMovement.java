package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * StockMovement entity representing inventory movement records.
 * Requirements: 4.1, 6.3 - Stock movement entity with relationships and constraints
 */
@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_stock_movements_item_id", columnList = "item_id"),
        @Index(name = "idx_stock_movements_user_id", columnList = "user_id"),
        @Index(name = "idx_stock_movements_movement_type", columnList = "movement_type"),
        @Index(name = "idx_stock_movements_created_at", columnList = "created_at"),
        @Index(name = "idx_stock_movements_reference", columnList = "reference_number")
})
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_movement_id")
    private Long stockMovementId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull(message = "Item is required")
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    @NotNull(message = "Movement type is required")
    private MovementType movementType;
    
    @Min(value = 1, message = "Quantity must be positive")
    @Column(name = "quantity", nullable = false)
    private Long quantity;
    
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "previous_stock", nullable = false)
    private Long previousStock;
    
    @Column(name = "new_stock", nullable = false)
    private Long newStock;
    
    @Column(name = "reason", length = 100)
    private String reason;
    
    @Column(name = "recipient", length = 255)
    private String recipient;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", length = 50)
    private StockOutReasonEnum reasonType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "source_mode", length = 50)
    private StockSourceMode sourceMode;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Default constructor for JPA
    public StockMovement() {}
    
    // Constructor for creating new stock movements
    public StockMovement(Item item, User user, MovementType movementType, Long quantity, 
                        Long previousStock, Long newStock) {
        this.item = item;
        this.user = user;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousStock = previousStock;
        this.newStock = newStock;
    }
    
    // Getters and setters
    public Long getStockMovementId() {
        return stockMovementId;
    }
    
    public void setStockMovementId(Long stockMovementId) {
        this.stockMovementId = stockMovementId;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public StockOutReasonEnum getReasonType() {
        return reasonType;
    }
    
    public void setReasonType(StockOutReasonEnum reasonType) {
        this.reasonType = reasonType;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public Warehouse getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    
    public StockSourceMode getSourceMode() { return sourceMode; }
    public void setSourceMode(StockSourceMode sourceMode) { this.sourceMode = sourceMode; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(stockMovementId, that.stockMovementId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(stockMovementId);
    }
    
    @Override
    public String toString() {
        return "StockMovement{" +
                "stockMovementId=" + stockMovementId +
                ", item=" + (item != null ? item.getSku() : null) +
                ", movementType=" + movementType +
                ", quantity=" + quantity +
                ", previousStock=" + previousStock +
                ", newStock=" + newStock +
                ", reason='" + reason + '\'' +
                ", recipient='" + recipient + '\'' +
                ", reasonType=" + reasonType +
                ", createdAt=" + createdAt +
                '}';
    }
}
