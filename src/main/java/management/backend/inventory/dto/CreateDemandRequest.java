package management.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class CreateDemandRequest {
    private Long employeeId;
    private Long itemId;
    private String unit;
    private String status;
    private String note;
    private java.util.List<ItemLine> items;
    
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
    public java.util.List<ItemLine> getItems() { return items; }
    public void setItems(java.util.List<ItemLine> items) { this.items = items; }
    
    public static class ItemLine {
        private Long itemId;
        private Integer units;
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Integer getUnits() { return units; }
        public void setUnits(Integer units) { this.units = units; }
    }
}
