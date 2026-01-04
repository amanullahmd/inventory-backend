package management.backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class StockInBatchRequest {
    private Long supplierId;
    @NotNull
    private Long warehouseId;
    private String notes;
    private String referenceNumber;
    @NotNull
    private List<StockInLine> items;
    
    public static class StockInLine {
        @NotNull
        private Long itemId;
        @NotNull
        @Min(1)
        private Long quantity;
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getQuantity() { return quantity; }
        public void setQuantity(Long quantity) { this.quantity = quantity; }
    }
    
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public List<StockInLine> getItems() { return items; }
    public void setItems(List<StockInLine> items) { this.items = items; }
}
