package management.backend.inventory.dto;

import java.util.List;

/**
 * DTO for aggregated reason breakdown response.
 * Used for reporting on stock-out reasons with summary statistics.
 * 
 * Requirements: SaaS Features - Stock-out reasons reporting
 */
public class ReasonBreakdownResponse {
    
    private Integer totalMovements;
    private Integer totalStockOut;
    private List<StockOutReasonResponse> reasons;
    private String periodStart;
    private String periodEnd;
    private String filterType; // "ALL", "ITEM", "CATEGORY"
    private Long filterId; // itemId or categoryId if applicable
    
    // Default constructor
    public ReasonBreakdownResponse() {}
    
    // Constructor with basic fields
    public ReasonBreakdownResponse(
        Integer totalMovements,
        Integer totalStockOut,
        List<StockOutReasonResponse> reasons
    ) {
        this.totalMovements = totalMovements;
        this.totalStockOut = totalStockOut;
        this.reasons = reasons;
    }
    
    // Constructor with all fields
    public ReasonBreakdownResponse(
        Integer totalMovements,
        Integer totalStockOut,
        List<StockOutReasonResponse> reasons,
        String periodStart,
        String periodEnd,
        String filterType,
        Long filterId
    ) {
        this.totalMovements = totalMovements;
        this.totalStockOut = totalStockOut;
        this.reasons = reasons;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.filterType = filterType;
        this.filterId = filterId;
    }
    
    // Getters and setters
    public Integer getTotalMovements() {
        return totalMovements;
    }
    
    public void setTotalMovements(Integer totalMovements) {
        this.totalMovements = totalMovements;
    }
    
    public Integer getTotalStockOut() {
        return totalStockOut;
    }
    
    public void setTotalStockOut(Integer totalStockOut) {
        this.totalStockOut = totalStockOut;
    }
    
    public List<StockOutReasonResponse> getReasons() {
        return reasons;
    }
    
    public void setReasons(List<StockOutReasonResponse> reasons) {
        this.reasons = reasons;
    }
    
    public String getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }
    
    public String getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public String getFilterType() {
        return filterType;
    }
    
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
    
    public Long getFilterId() {
        return filterId;
    }
    
    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }
    
    @Override
    public String toString() {
        return "ReasonBreakdownResponse{" +
                "totalMovements=" + totalMovements +
                ", totalStockOut=" + totalStockOut +
                ", reasonCount=" + (reasons != null ? reasons.size() : 0) +
                ", periodStart='" + periodStart + '\'' +
                ", periodEnd='" + periodEnd + '\'' +
                ", filterType='" + filterType + '\'' +
                '}';
    }
}
