package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "purchase_order_items", indexes = {
    @Index(name = "idx_purchase_order_items_po", columnList = "purchase_order_id")
})
public class PurchaseOrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_item_id")
    private Long purchaseOrderItemId;
    
    @NotNull(message = "Purchase order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    
    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "received_quantity")
    private Integer receivedQuantity = 0;
    
    public PurchaseOrderItem() {}
    
    public PurchaseOrderItem(PurchaseOrder purchaseOrder, Item item, Integer quantity, BigDecimal unitPrice) {
        this.purchaseOrder = purchaseOrder;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.receivedQuantity = 0;
    }
    
    // Getters and Setters
    public Long getPurchaseOrderItemId() {
        return purchaseOrderItemId;
    }
    
    public void setPurchaseOrderItemId(Long purchaseOrderItemId) {
        this.purchaseOrderItemId = purchaseOrderItemId;
    }
    
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }
    
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public Batch getBatch() {
        return batch;
    }
    
    public void setBatch(Batch batch) {
        this.batch = batch;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }
    
    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
    
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
    
    public Integer getPendingQuantity() {
        return quantity - (receivedQuantity != null ? receivedQuantity : 0);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderItem that = (PurchaseOrderItem) o;
        return Objects.equals(purchaseOrderItemId, that.purchaseOrderItemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderItemId);
    }
    
    @Override
    public String toString() {
        return "PurchaseOrderItem{" +
                "purchaseOrderItemId=" + purchaseOrderItemId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", receivedQuantity=" + receivedQuantity +
                '}';
    }
}
