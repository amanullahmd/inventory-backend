package management.backend.inventory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {
    private Long employeeId;
    private String employeeCode;
    private String name;
    private String grade;
    private String position;
    private Long branchId;
    private String branchName;
    private String mobileNumber;
    private String email;
    private String address;
    private String servicePeriod;
    private String nidNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public EmployeeResponse(Long employeeId, String employeeCode, String name, String grade, String position,
                            Long branchId, String branchName, String mobileNumber, String email, String address,
                            String servicePeriod, String nidNumber, LocalDate dateOfBirth, String gender, String nationality,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.name = name;
        this.grade = grade;
        this.position = position;
        this.branchId = branchId;
        this.branchName = branchName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.servicePeriod = servicePeriod;
        this.nidNumber = nidNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.nationality = nationality;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeCode() { return employeeCode; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
    public String getPosition() { return position; }
    public Long getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getServicePeriod() { return servicePeriod; }
    public String getNidNumber() { return nidNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getNationality() { return nationality; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
