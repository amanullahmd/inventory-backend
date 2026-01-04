package management.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class CreateDemandRequest {
    @NotNull
    private String demanderName;
    private String position;
    private String grade;
    @NotNull
    private Long itemId;
    private String unit;
    private Long warehouseId;
    private String status;
    private String note;
    private java.util.List<ItemLine> items;
    
    public String getDemanderName() { return demanderName; }
    public void setDemanderName(String demanderName) { this.demanderName = demanderName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public java.util.List<ItemLine> getItems() { return items; }
    public void setItems(java.util.List<ItemLine> items) { this.items = items; }
    
    public static class ItemLine {
        private Long itemId;
        private Integer quantity;
        private String unit;
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}
