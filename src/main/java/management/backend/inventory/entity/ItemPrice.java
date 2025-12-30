package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "item_prices", indexes = {
    @Index(name = "idx_item_prices_item_id", columnList = "item_id"),
    @Index(name = "idx_item_prices_active", columnList = "is_active")
})
public class ItemPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;
    
    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(name = "price_type", length = 30)
    private String priceType = "RETAIL";
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(name = "currency", length = 10)
    private String currency = "EUR";
    
    @Column(name = "effective_from")
    private LocalDate effectiveFrom = LocalDate.now();
    
    @Column(name = "effective_to")
    private LocalDate effectiveTo;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (effectiveFrom == null) {
            effectiveFrom = LocalDate.now();
        }
    }
    
    public ItemPrice() {}
    
    public ItemPrice(Item item, BigDecimal price) {
        this.item = item;
        this.price = price;
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getPriceId() {
        return priceId;
    }
    
    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public String getPriceType() {
        return priceType;
    }
    
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }
    
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
    
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
    
    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isEffective() {
        LocalDate today = LocalDate.now();
        boolean afterStart = today.isEqual(effectiveFrom) || today.isAfter(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || today.isBefore(effectiveTo) || today.isEqual(effectiveTo);
        return afterStart && beforeEnd && isActive;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPrice itemPrice = (ItemPrice) o;
        return Objects.equals(priceId, itemPrice.priceId) &&
               Objects.equals(item, itemPrice.item) &&
               Objects.equals(priceType, itemPrice.priceType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(priceId, item, priceType);
    }
    
    @Override
    public String toString() {
        return "ItemPrice{" +
                "priceId=" + priceId +
                ", priceType='" + priceType + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", isActive=" + isActive +
                '}';
    }
}
