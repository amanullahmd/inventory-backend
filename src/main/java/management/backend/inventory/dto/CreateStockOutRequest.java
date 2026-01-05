package management.backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import management.backend.inventory.entity.StockOutType;

import java.time.LocalDateTime;

@Data
public class CreateStockOutRequest {
    @NotNull(message = "Stock out type is required")
    private StockOutType stockOutType;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String note;
    
    private Long sourceWarehouseId;
    private Long branchId;
    private Long employeeId;
    
    private LocalDateTime date; // Optional, defaults to now
}
