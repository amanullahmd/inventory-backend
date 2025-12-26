package management.backend.inventory.controller;

import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.ReasonBreakdownResponse;
import management.backend.inventory.dto.StockMovementRequest;
import management.backend.inventory.dto.StockOutReasonResponse;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.service.StockOutReasonService;
import management.backend.inventory.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for stock movement endpoints.
 * Requirements: 8.4, 8.5 - Stock-in/out operations and stock summary retrieval
 * SaaS Features: Stock-out reasons tracking and reporting
 * Feature: inventory-backend-oauth2
 */
@RestController
@RequestMapping("/stock")
@Validated
public class StockController {
    
    private final StockService stockService;
    private final StockOutReasonService stockOutReasonService;
    
    public StockController(StockService stockService, StockOutReasonService stockOutReasonService) {
        this.stockService = stockService;
        this.stockOutReasonService = stockOutReasonService;
    }
    
    /**
     * POST /api/stock/in - Record stock-in movement.
     * Requirements: 8.4 - Stock-in operation recording
     * Accessible to User role only (Admins cannot perform stock operations)
     */
    @PostMapping("/in")
    @PreAuthorize("hasRole('USER') and !hasRole('ADMIN')")
    public ResponseEntity<StockMovement> recordStockIn(@Valid @RequestBody StockMovementRequest request, Authentication authentication) {
        StockMovement movement = stockService.recordStockIn(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }
    
    /**
     * POST /api/stock/out - Record stock-out movement with validation.
     * Requirements: 8.5 - Stock-out operation with validation
     * SaaS Features: Stock-out reason tracking
     * Accessible to User role only (Admins cannot perform stock operations)
     */
    @PostMapping("/out")
    @PreAuthorize("hasRole('USER') and !hasRole('ADMIN')")
    public ResponseEntity<StockMovement> recordStockOut(@Valid @RequestBody StockMovementRequest request, Authentication authentication) {
        StockMovement movement = stockService.recordStockOut(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }
    
    /**
     * GET /api/stock - Retrieve stock summary for all items.
     * Requirements: 8.4, 8.5 - Stock summary retrieval
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ItemStockResponse>> getStockSummary() {
        List<ItemStockResponse> stockSummary = stockService.getStockSummaryForAllItems();
        return ResponseEntity.ok(stockSummary);
    }
    
    /**
     * GET /api/stock/{itemId} - Retrieve stock summary for a specific item.
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemStockResponse> getStockSummaryForItem(@PathVariable Long itemId) {
        return stockService.getStockSummaryForItem(itemId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/stock/reasons - Get predefined stock-out reasons.
     * SaaS Features: Stock-out reasons list
     * Accessible to authenticated users
     */
    @GetMapping("/reasons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockOutReasonResponse>> getPredefinedReasons() {
        List<StockOutReasonResponse> reasons = stockOutReasonService.getPredefinedReasons();
        return ResponseEntity.ok(reasons);
    }
    
    /**
     * GET /api/stock/reasons/breakdown - Get stock-out reasons breakdown report.
     * SaaS Features: Stock-out reasons reporting
     * Accessible to authenticated users
     */
    @GetMapping("/reasons/breakdown")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReasonBreakdownResponse> getReasonBreakdown(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        List<StockOutReasonResponse> reasons = stockOutReasonService.getReasonBreakdown(start, end);
        Integer total = reasons.stream().mapToInt(StockOutReasonResponse::getCount).sum();
        
        ReasonBreakdownResponse response = new ReasonBreakdownResponse(
            total,
            total,
            reasons,
            start.toString(),
            end.toString(),
            "ALL",
            null
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/stock/movements/history - Get stock movement history with reasons.
     * SaaS Features: Stock movement history
     * Accessible to authenticated users
     */
    @GetMapping("/movements/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockMovement>> getMovementHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<StockMovement> movements = stockService.getRecentStockMovements();
        return ResponseEntity.ok(movements);
    }
}
