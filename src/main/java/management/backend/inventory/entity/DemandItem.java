package management.backend.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "demand_items", indexes = {
    @Index(name = "idx_demand_items_demand", columnList = "demand_id"),
    @Index(name = "idx_demand_items_item", columnList = "item_id")
})
public class DemandItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_item_id")
    private Long demandItemId;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id", nullable = false)
    private Demand demand;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @NotNull
    @Min(1)
    @Column(name = "units", nullable = false)
    private Integer units = 1;
    
    public DemandItem() {}
    public DemandItem(Demand demand, Item item, Integer units) {
        this.demand = demand;
        this.item = item;
        this.units = units != null ? units : 1;
    }
    
    public Long getDemandItemId() { return demandItemId; }
    public Demand getDemand() { return demand; }
    public void setDemand(Demand demand) { this.demand = demand; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
}
