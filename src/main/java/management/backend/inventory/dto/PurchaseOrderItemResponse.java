package management.backend.inventory.dto;

import java.math.BigDecimal;

public class PurchaseOrderItemResponse {
    private Long purchaseOrderItemId;
    private Long itemId;
    private String sku;
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    public PurchaseOrderItemResponse(Long purchaseOrderItemId, Long itemId, String sku, String name, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.purchaseOrderItemId = purchaseOrderItemId;
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }
    public Long getPurchaseOrderItemId() { return purchaseOrderItemId; }
    public Long getItemId() { return itemId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
