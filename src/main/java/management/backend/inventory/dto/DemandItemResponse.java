package management.backend.inventory.dto;

public class DemandItemResponse {
    private Long demandItemId;
    private Long itemId;
    private String sku;
    private String name;
    private Integer quantity;
    private String unit;
    public DemandItemResponse(Long demandItemId, Long itemId, String sku, String name, Integer quantity, String unit) {
        this.demandItemId = demandItemId;
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
    public Long getDemandItemId() { return demandItemId; }
    public Long getItemId() { return itemId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
