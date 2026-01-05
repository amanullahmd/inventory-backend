package management.backend.inventory.dto;

import lombok.Builder;
import lombok.Data;
import management.backend.inventory.entity.StockOutType;

import java.time.LocalDateTime;

@Data
@Builder
public class StockOutResponse {
    private Long id;
    private StockOutType stockOutType;
    private Long itemId;
    private String itemName;
    private String itemSku;
    private Integer quantity;
    private LocalDateTime stockOutDate;
    private String note;
    
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long branchId;
    private String branchName;
    private Long employeeId;
    private String employeeName;
    private String referenceNumber;
}
