package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@org.hibernate.annotations.DynamicUpdate
@Table(name = "purchase_orders", indexes = {
    @Index(name = "idx_purchase_orders_supplier", columnList = "supplier_id"),
    @Index(name = "idx_purchase_orders_warehouse", columnList = "warehouse_id"),
    @Index(name = "idx_purchase_orders_status", columnList = "status")
})
public class PurchaseOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_id")
    private Long purchaseOrderId;
    
    @NotNull(message = "Supplier is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    
    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    
    @NotNull(message = "Order date is required")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;
    
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @NotNull(message = "Created by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "purchase_order_code", length = 50, unique = true, nullable = false)
    private String purchaseOrderCode;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public PurchaseOrder() {}
    
    public PurchaseOrder(Supplier supplier, Warehouse warehouse, LocalDate orderDate, User createdBy) {
        this.supplier = supplier;
        this.warehouse = warehouse;
        this.orderDate = orderDate;
        this.createdBy = createdBy;
        this.status = PurchaseOrderStatus.DRAFT;
    }
    
    // Getters and Setters
    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }
    
    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public Warehouse getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    
    public PurchaseOrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public List<PurchaseOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getPurchaseOrderCode() { return purchaseOrderCode; }
    public void setPurchaseOrderCode(String purchaseOrderCode) { this.purchaseOrderCode = purchaseOrderCode; }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return Objects.equals(purchaseOrderId, that.purchaseOrderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderId);
    }
    
    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "purchaseOrderId=" + purchaseOrderId +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
