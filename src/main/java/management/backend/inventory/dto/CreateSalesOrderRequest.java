package management.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class CreateSalesOrderRequest {
    @NotNull
    private Long warehouseId;
    @NotNull
    private String orderDate; // ISO date
    private String deliveryDate; // ISO date
    private String customerName;
    private String customerEmail;
    private String notes;

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
