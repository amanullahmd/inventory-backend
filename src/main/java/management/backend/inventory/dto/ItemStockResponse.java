package management.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemStockResponse {
    
    private Long itemId;
    private String name;
    private String sku;
    private String description;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
    private Integer currentStock;
    private Integer totalStockIn;
    private Integer totalStockOut;
    private Long categoryId;
    private String categoryName;
    private Long minimumStock;
    private Long maximumStock;
    private Long reorderLevel;
    
    // Default constructor
    public ItemStockResponse() {}
    
    // Constructor
    public ItemStockResponse(Long itemId, String name, String sku, String description, BigDecimal unitPrice,
                             LocalDateTime createdAt, Integer currentStock,
                             Integer totalStockIn, Integer totalStockOut,
                             Long categoryId, String categoryName,
                             Long minimumStock, Long maximumStock, Long reorderLevel) {
        this.itemId = itemId;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
        this.currentStock = currentStock;
        this.totalStockIn = totalStockIn;
        this.totalStockOut = totalStockOut;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.minimumStock = minimumStock;
        this.maximumStock = maximumStock;
        this.reorderLevel = reorderLevel;
    }
    
    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public Integer getTotalStockIn() {
        return totalStockIn;
    }
    
    public void setTotalStockIn(Integer totalStockIn) {
        this.totalStockIn = totalStockIn;
    }
    
    public Integer getTotalStockOut() {
        return totalStockOut;
    }
    
    public void setTotalStockOut(Integer totalStockOut) {
        this.totalStockOut = totalStockOut;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Long getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Long minimumStock) { this.minimumStock = minimumStock; }
    public Long getMaximumStock() { return maximumStock; }
    public void setMaximumStock(Long maximumStock) { this.maximumStock = maximumStock; }
    public Long getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Long reorderLevel) { this.reorderLevel = reorderLevel; }
    
    @Override
    public String toString() {
        return "ItemStockResponse{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", unitPrice=" + unitPrice +
                ", currentStock=" + currentStock +
                ", totalStockIn=" + totalStockIn +
                ", totalStockOut=" + totalStockOut +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
