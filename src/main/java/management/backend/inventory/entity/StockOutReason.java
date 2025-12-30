package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock_out_reasons", indexes = {
    @Index(name = "idx_stock_out_reasons_active", columnList = "is_active")
})
public class StockOutReason {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_id")
    private Long reasonId;
    
    @NotBlank(message = "Reason name is required")
    @Column(name = "reason_name", nullable = false, unique = true, length = 100)
    private String reasonName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public StockOutReason() {}
    
    public StockOutReason(String reasonName, String description) {
        this.reasonName = reasonName;
        this.description = description;
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getReasonId() {
        return reasonId;
    }
    
    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }
    
    public String getReasonName() {
        return reasonName;
    }
    
    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockOutReason that = (StockOutReason) o;
        return Objects.equals(reasonId, that.reasonId) &&
               Objects.equals(reasonName, that.reasonName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reasonId, reasonName);
    }
    
    @Override
    public String toString() {
        return "StockOutReason{" +
                "reasonId=" + reasonId +
                ", reasonName='" + reasonName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
