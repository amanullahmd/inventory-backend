package management.backend.inventory.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateWarehouseRequest {
    @NotBlank
    private String name;
    private String address;
    private Integer capacityUnits;
    private String warehouseCode;
    private Boolean isActive;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getCapacityUnits() { return capacityUnits; }
    public void setCapacityUnits(Integer capacityUnits) { this.capacityUnits = capacityUnits; }
    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
