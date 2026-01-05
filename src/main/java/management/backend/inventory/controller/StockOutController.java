package management.backend.inventory.controller;

import jakarta.validation.Valid;
import management.backend.inventory.dto.CreateStockOutBatchRequest;
import management.backend.inventory.dto.CreateStockOutRequest;
import management.backend.inventory.dto.StockOutResponse;
import management.backend.inventory.service.StockOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-outs")
// Controller for managing stock outs
public class StockOutController {

    private final StockOutService stockOutService;

    public StockOutController(StockOutService stockOutService) {
        this.stockOutService = stockOutService;
    }

    @PostMapping("/batch")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockOutResponse>> createStockOutBatch(@Valid @RequestBody CreateStockOutBatchRequest request) {
        return ResponseEntity.ok(stockOutService.createStockOutBatch(request));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<StockOutResponse> createStockOut(@Valid @RequestBody CreateStockOutRequest request) {
        return ResponseEntity.ok(stockOutService.createStockOut(request));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<StockOutResponse> updateStockOut(
            @PathVariable Long id,
            @Valid @RequestBody CreateStockOutRequest request) {
        return ResponseEntity.ok(stockOutService.updateStockOut(id, request));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteStockOut(@PathVariable Long id) {
        stockOutService.deleteStockOut(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockOutResponse>> getAllStockOuts() {
        return ResponseEntity.ok(stockOutService.getAllStockOuts());
    }

    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<StockOutResponse> getStockOutById(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.getStockOutById(id));
    }
}
