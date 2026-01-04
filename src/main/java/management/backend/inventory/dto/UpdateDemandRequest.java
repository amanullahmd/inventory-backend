package management.backend.inventory.dto;

public class UpdateDemandRequest {
    private String demandCode;
    private Long employeeId;
    private Long itemId;
    private String unit;
    private String status;
    private String note;
    private java.util.List<CreateDemandRequest.ItemLine> items;
    public String getDemandCode() { return demandCode; }
    public void setDemandCode(String demandCode) { this.demandCode = demandCode; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public java.util.List<CreateDemandRequest.ItemLine> getItems() { return items; }
    public void setItems(java.util.List<CreateDemandRequest.ItemLine> items) { this.items = items; }
}
