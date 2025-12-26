package management.backend.inventory.dto;

import java.math.BigDecimal;

/**
 * DTO for dashboard statistics response.
 * Requirements: 4.1, 4.2, 4.3, 4.4
 */
public class StatisticsResponse {
    
    private Integer totalItems;
    private BigDecimal totalValue;
    private Integer lowStockItems;
    private Integer outOfStockItems;
    
    // Constructors
    public StatisticsResponse() {}
    
    public StatisticsResponse(Integer totalItems, BigDecimal totalValue, Integer lowStockItems, Integer outOfStockItems) {
        this.totalItems = totalItems;
        this.totalValue = totalValue;
        this.lowStockItems = lowStockItems;
        this.outOfStockItems = outOfStockItems;
    }
    
    // Getters and Setters
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public Integer getLowStockItems() {
        return lowStockItems;
    }
    
    public void setLowStockItems(Integer lowStockItems) {
        this.lowStockItems = lowStockItems;
    }
    
    public Integer getOutOfStockItems() {
        return outOfStockItems;
    }
    
    public void setOutOfStockItems(Integer outOfStockItems) {
        this.outOfStockItems = outOfStockItems;
    }
    
    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "totalItems=" + totalItems +
                ", totalValue=" + totalValue +
                ", lowStockItems=" + lowStockItems +
                ", outOfStockItems=" + outOfStockItems +
                '}';
    }
}
