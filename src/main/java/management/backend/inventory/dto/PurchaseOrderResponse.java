package management.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseOrderResponse {
    private Long purchaseOrderId;
    private String purchaseOrderCode;
    private Long supplierId;
    private String supplierName;
    private Long warehouseId;
    private String warehouseName;
    private String status;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    
    public PurchaseOrderResponse(Long purchaseOrderId, String purchaseOrderCode, Long supplierId, String supplierName,
                                 Long warehouseId, String warehouseName, String status,
                                 LocalDate orderDate, LocalDate expectedDeliveryDate,
                                 BigDecimal totalAmount, String notes, LocalDateTime createdAt) {
        this.purchaseOrderId = purchaseOrderId;
        this.purchaseOrderCode = purchaseOrderCode;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.status = status;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public String getPurchaseOrderCode() { return purchaseOrderCode; }
    public Long getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public Long getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public String getStatus() { return status; }
    public LocalDate getOrderDate() { return orderDate; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
