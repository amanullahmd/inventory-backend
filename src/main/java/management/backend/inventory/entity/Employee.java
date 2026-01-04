package management.backend.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employees_name", columnList = "name"),
        @Index(name = "idx_employees_branch", columnList = "branch_id")
})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;
    
    @Column(name = "employee_code", unique = true, length = 100)
    private String employeeCode;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "grade", length = 100)
    private String grade;
    
    @Column(name = "position", length = 150)
    private String position;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Warehouse branch;
    
    @Column(name = "mobile_number", length = 50)
    private String mobileNumber;
    
    @Column(name = "email", length = 200)
    private String email;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "service_period", length = 100)
    private String servicePeriod;
    
    @Column(name = "nid_number", length = 100)
    private String nidNumber;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
    
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Warehouse getBranch() { return branch; }
    public void setBranch(Warehouse branch) { this.branch = branch; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getServicePeriod() { return servicePeriod; }
    public void setServicePeriod(String servicePeriod) { this.servicePeriod = servicePeriod; }
    public String getNidNumber() { return nidNumber; }
    public void setNidNumber(String nidNumber) { this.nidNumber = nidNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
