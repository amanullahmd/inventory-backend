package management.backend.inventory.dto;

import java.time.LocalDateTime;

public class DemandResponse {
    private Long demandId;
    private String demandCode;
    private String demanderName;
    private String position;
    private String grade;
    private String status;
    private Long itemId;
    private String itemName;
    private String sku;
    private String unit;
    private Long warehouseId;
    private String warehouseName;
    private String note;
    private String requestedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private java.util.List<DemandItemResponse> items;
    
    public DemandResponse(Long demandId, String demandCode, String demanderName, String position, String grade, String status,
                          Long itemId, String itemName, String sku, String unit,
                          Long warehouseId, String warehouseName, String requestedByName, LocalDateTime createdAt, LocalDateTime updatedAt, String note,
                          java.util.List<DemandItemResponse> items) {
        this.demandId = demandId;
        this.demandCode = demandCode;
        this.demanderName = demanderName;
        this.position = position;
        this.grade = grade;
        this.status = status;
        this.itemId = itemId;
        this.itemName = itemName;
        this.sku = sku;
        this.unit = unit;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.requestedByName = requestedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.note = note;
        this.items = items;
    }
    
    public Long getDemandId() { return demandId; }
    public String getDemandCode() { return demandCode; }
    public String getDemanderName() { return demanderName; }
    public String getPosition() { return position; }
    public String getGrade() { return grade; }
    public String getStatus() { return status; }
    public Long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getSku() { return sku; }
    public String getUnit() { return unit; }
    public Long getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public String getRequestedByName() { return requestedByName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getNote() { return note; }
    public java.util.List<DemandItemResponse> getItems() { return items; }
}
