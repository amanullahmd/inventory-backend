package management.backend.inventory.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateWarehouseRequest {
    @NotBlank
    private String name;
    private String address;
    private Integer capacityUnits;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getCapacityUnits() { return capacityUnits; }
    public void setCapacityUnits(Integer capacityUnits) { this.capacityUnits = capacityUnits; }
}
