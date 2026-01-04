package management.backend.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class AddPurchaseOrderItemsRequest {
    @NotNull
    private List<Line> items;
    private String notes;
    public List<Line> getItems() { return items; }
    public void setItems(List<Line> items) { this.items = items; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public static class Line {
        @NotNull
        private Long itemId;
        @NotNull
        @Min(1)
        private Integer quantity;
        @NotNull
        @DecimalMin("0.01")
        private BigDecimal unitPrice;
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
}
