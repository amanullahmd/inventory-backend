package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@org.hibernate.annotations.DynamicUpdate
@Table(name = "demands", indexes = {
    @Index(name = "idx_demands_item", columnList = "item_id"),
    @Index(name = "idx_demands_requested_by", columnList = "requested_by")
})
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;
    
    @Column(name = "demand_code", length = 50, unique = true)
    private String demandCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private DemandStatus status = DemandStatus.DRAFT;
    
    @Column(name = "demander_name", nullable = false)
    private String demanderName;
    
    @Column(name = "position")
    private String position;
    
    @Column(name = "grade")
    private String grade;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(name = "unit", length = 50)
    private String unit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
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
    
    public Long getDemandId() { return demandId; }
    public String getDemandCode() { return demandCode; }
    public void setDemandCode(String demandCode) { this.demandCode = demandCode; }
    public String getDemanderName() { return demanderName; }
    public void setDemanderName(String demanderName) { this.demanderName = demanderName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public DemandStatus getStatus() { return status; }
    public void setStatus(DemandStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
