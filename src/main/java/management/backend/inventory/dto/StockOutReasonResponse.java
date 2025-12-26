package management.backend.inventory.dto;

/**
 * DTO for stock-out reason response data.
 * Used for reporting and breakdown analysis.
 * 
 * Requirements: SaaS Features - Stock-out reasons reporting
 */
public class StockOutReasonResponse {
    
    private String reasonType;
    private String reasonLabel;
    private String description;
    private Integer count;
    private Double percentage;
    
    // Default constructor
    public StockOutReasonResponse() {}
    
    // Constructor with all fields
    public StockOutReasonResponse(String reasonType, String reasonLabel, String description, Integer count, Double percentage) {
        this.reasonType = reasonType;
        this.reasonLabel = reasonLabel;
        this.description = description;
        this.count = count;
        this.percentage = percentage;
    }
    
    // Constructor for query results
    public StockOutReasonResponse(String reasonType, String reasonLabel, Integer count) {
        this.reasonType = reasonType;
        this.reasonLabel = reasonLabel;
        this.count = count;
        this.percentage = 0.0;
    }
    
    // Getters and setters
    public String getReasonType() {
        return reasonType;
    }
    
    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }
    
    public String getReasonLabel() {
        return reasonLabel;
    }
    
    public void setReasonLabel(String reasonLabel) {
        this.reasonLabel = reasonLabel;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    public Double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public String toString() {
        return "StockOutReasonResponse{" +
                "reasonType='" + reasonType + '\'' +
                ", reasonLabel='" + reasonLabel + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                '}';
    }
}
