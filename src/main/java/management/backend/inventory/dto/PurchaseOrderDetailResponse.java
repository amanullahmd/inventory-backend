package management.backend.inventory.dto;

import java.util.List;

public class PurchaseOrderDetailResponse extends PurchaseOrderResponse {
    private List<PurchaseOrderItemResponse> items;
    public PurchaseOrderDetailResponse(Long purchaseOrderId, String purchaseOrderCode, Long supplierId, String supplierName,
                                       Long warehouseId, String warehouseName, String status,
                                       java.time.LocalDate orderDate, java.time.LocalDate expectedDeliveryDate,
                                       java.math.BigDecimal totalAmount, String notes, java.time.LocalDateTime createdAt,
                                       List<PurchaseOrderItemResponse> items) {
        super(purchaseOrderId, purchaseOrderCode, supplierId, supplierName, warehouseId, warehouseName, status,
              orderDate, expectedDeliveryDate, totalAmount, notes, createdAt);
        this.items = items;
    }
    public List<PurchaseOrderItemResponse> getItems() { return items; }
}
