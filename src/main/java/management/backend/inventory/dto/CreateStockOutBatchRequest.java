package management.backend.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import management.backend.inventory.entity.StockOutType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateStockOutBatchRequest {
    @NotNull(message = "Stock out type is required")
    private StockOutType stockOutType;

    private Long sourceWarehouseId;
    private Long branchId;
    private Long employeeId;
    private String note;
    private LocalDateTime date;

    @NotNull(message = "Items list is required")
    @Valid
    private List<StockOutItemRequest> items;
}
