package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Item entity representing inventory items with validation constraints.
 * Requirements: 3.1, 6.2 - Item entity with JPA annotations and database constraints
 */
@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_items_category_id", columnList = "category_id"),
        @Index(name = "idx_items_sku", columnList = "sku"),
        @Index(name = "idx_items_name", columnList = "name"),
        @Index(name = "idx_items_is_active", columnList = "is_active"),
        @Index(name = "idx_items_current_stock", columnList = "current_stock")
})
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @NotBlank(message = "Name is required and cannot be blank")
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @NotBlank(message = "SKU is required and cannot be blank")
    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @DecimalMin(value = "0.00", message = "Unit price must be non-negative")
    @Column(name = "unit_price", nullable = true, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    
    @Min(value = 0, message = "Current stock must be non-negative")
    @Column(name = "current_stock", nullable = false)
    private Long currentStock = 0L;
    
    @Min(value = 0, message = "Minimum stock must be non-negative")
    @Column(name = "minimum_stock", nullable = false)
    private Long minimumStock = 0L;
    
    @Column(name = "maximum_stock")
    private Long maximumStock;
    
    @Column(name = "reorder_level")
    private Long reorderLevel;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Default constructor for JPA
    public Item() {}
    
    // Constructor for creating new items
    public Item(String name, String sku, BigDecimal unitPrice) {
        this.name = name;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.currentStock = 0L;
        this.minimumStock = 0L;
        this.isActive = true;
    }
    
    // Getters and setters
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Long getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Long currentStock) {
        this.currentStock = currentStock;
    }
    
    public Long getMinimumStock() {
        return minimumStock;
    }
    
    public void setMinimumStock(Long minimumStock) {
        this.minimumStock = minimumStock;
    }
    
    public Long getMaximumStock() {
        return maximumStock;
    }
    
    public void setMaximumStock(Long maximumStock) {
        this.maximumStock = maximumStock;
    }
    
    public Long getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Long reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId) &&
               Objects.equals(sku, item.sku);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemId, sku);
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", unitPrice=" + unitPrice +
                ", currentStock=" + currentStock +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
