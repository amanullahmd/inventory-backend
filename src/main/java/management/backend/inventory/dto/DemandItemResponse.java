package management.backend.inventory.dto;

public class DemandItemResponse {
    private Long demandItemId;
    private Long itemId;
    private String sku;
    private String name;
    private Integer units;
    public DemandItemResponse(Long demandItemId, Long itemId, String sku, String name, Integer units) {
        this.demandItemId = demandItemId;
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.units = units;
    }
    public Long getDemandItemId() { return demandItemId; }
    public Long getItemId() { return itemId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getUnits() { return units; }
}
