package management.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class CreateEmployeeRequest {
    @NotNull
    private String name;
    private String grade;
    private String position;
    private Long branchId;
    private String mobileNumber;
    private String email;
    private String address;
    private String servicePeriod;
    private String nidNumber;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
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
}
