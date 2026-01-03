package management.backend.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for creating new inventory items.
 * Requirements: 3.1, 8.2 - Item creation request with validation
 */
public class CreateItemRequest {
    
    @NotBlank(message = "Name is required and cannot be blank")
    private String name;
    
    @NotBlank(message = "SKU is required and cannot be blank")
    private String sku;
    
    private BigDecimal unitPrice;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private String description;
    
    @Min(value = 0, message = "Minimum stock must be non-negative")
    private Long minimumStock;
    
    @Min(value = 0, message = "Maximum stock must be non-negative")
    private Long maximumStock;
    
    @Min(value = 0, message = "Reorder level must be non-negative")
    private Long reorderLevel;
    
    // Default constructor
    public CreateItemRequest() {}
    
    // Constructor
    public CreateItemRequest(String name, String sku, BigDecimal unitPrice) {
        this.name = name;
        this.sku = sku;
        this.unitPrice = unitPrice;
    }
    
    // Constructor with category
    public CreateItemRequest(String name, String sku, BigDecimal unitPrice, Long categoryId) {
        this.name = name;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.categoryId = categoryId;
    }
    
    // Getters and setters
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
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    @Override
    public String toString() {
        return "CreateItemRequest{" +
                "name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", unitPrice=" + unitPrice +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                ", minimumStock=" + minimumStock +
                ", maximumStock=" + maximumStock +
                ", reorderLevel=" + reorderLevel +
                '}';
    }
}
