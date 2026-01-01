package management.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class CreatePurchaseOrderRequest {
    @NotNull
    private Long supplierId;
    @NotNull
    private Long warehouseId;
    @NotNull
    private String orderDate; // ISO date
    private String expectedDeliveryDate; // ISO date
    private String notes;

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(String expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
