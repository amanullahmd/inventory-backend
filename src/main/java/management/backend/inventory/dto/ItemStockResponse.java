package management.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemStockResponse {
    
    private Long itemId;
    private String name;
    private String sku;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
    private Integer currentStock;
    private Integer totalStockIn;
    private Integer totalStockOut;
    
    // Default constructor
    public ItemStockResponse() {}
    
    // Constructor
    public ItemStockResponse(Long itemId, String name, String sku, BigDecimal unitPrice, 
                           LocalDateTime createdAt, Integer currentStock, 
                           Integer totalStockIn, Integer totalStockOut) {
        this.itemId = itemId;
        this.name = name;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
        this.currentStock = currentStock;
        this.totalStockIn = totalStockIn;
        this.totalStockOut = totalStockOut;
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
                '}';
    }
}
