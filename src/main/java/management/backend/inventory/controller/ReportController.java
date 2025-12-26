package management.backend.inventory.controller;

import management.backend.inventory.dto.StockMovementHistoryResponse;
import management.backend.inventory.dto.StockOutReasonResponse;
import management.backend.inventory.entity.AuditLog;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.repository.AuditLogRepository;
import management.backend.inventory.repository.StockMovementRepository;
import management.backend.inventory.service.StockOutReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for reporting endpoints.
 * Requirements: SaaS Features - Comprehensive reporting and analytics
 * Feature: inventory-backend-oauth2
 */
@RestController
@RequestMapping("/reports")
@Validated
@Tag(name = "Reports", description = "Reporting and analytics endpoints")
public class ReportController {
    
    private final StockOutReasonService stockOutReasonService;
    private final StockMovementRepository stockMovementRepository;
    private final AuditLogRepository auditLogRepository;
    
    public ReportController(
            StockOutReasonService stockOutReasonService,
            StockMovementRepository stockMovementRepository,
            AuditLogRepository auditLogRepository) {
        this.stockOutReasonService = stockOutReasonService;
        this.stockMovementRepository = stockMovementRepository;
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * GET /api/reports/stock-out-reasons - Get stock-out reasons breakdown report.
     * SaaS Features: Stock-out reasons reporting
     * Accessible to authenticated users
     */
    @GetMapping("/stock-out-reasons")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get stock-out reasons report", description = "Retrieve breakdown of stock-out reasons with counts and percentages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<StockOutReasonResponse>> getStockOutReasonsReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long itemId) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        List<StockOutReasonResponse> reasons;
        if (itemId != null) {
            reasons = stockOutReasonService.getReasonBreakdownByItem(itemId, start, end);
        } else {
            reasons = stockOutReasonService.getReasonBreakdown(start, end);
        }
        
        return ResponseEntity.ok(reasons);
    }
    
    /**
     * GET /api/reports/stock-movements - Get stock movement history report.
     * SaaS Features: Stock movement history reporting
     * Accessible to authenticated users
     */
    @GetMapping("/stock-movements")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get stock movements report", description = "Retrieve stock movement history with reasons and recipients")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<StockMovementHistoryResponse>> getStockMovementsReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String movementType,
            @RequestParam(required = false) Long itemId) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        // Get all stock movements
        List<StockMovement> movements = stockMovementRepository.findAll();
        
        // Filter by date range
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        List<StockMovementHistoryResponse> report = movements.stream()
            .filter(m -> m.getCreatedAt() != null && 
                    m.getCreatedAt().isAfter(startDateTime) && 
                    m.getCreatedAt().isBefore(endDateTime))
            .filter(m -> movementType == null || m.getMovementType().toString().equals(movementType))
            .filter(m -> itemId == null || m.getItem().getItemId().equals(itemId))
            .map(this::convertToHistoryResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(report);
    }
    
    /**
     * GET /api/reports/audit-log - Get audit log report.
     * Requirements: 5.4 - Audit trail for compliance
     * Accessible to ADMIN role only
     */
    @GetMapping("/audit-log")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit log report", description = "Retrieve audit log entries for compliance and debugging")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<AuditLog>> getAuditLogReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String entityType) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        // Get audit logs within date range
        List<AuditLog> logs = auditLogRepository.findByDateRange(startDateTime, endDateTime);
        
        // Filter by userId if provided
        if (userId != null) {
            logs = logs.stream()
                .filter(log -> log.getUser() != null && log.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        }
        
        // Filter by entityType if provided
        if (entityType != null) {
            logs = logs.stream()
                .filter(log -> log.getEntityType().equals(entityType))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Helper method to convert StockMovement to StockMovementHistoryResponse.
     */
    private StockMovementHistoryResponse convertToHistoryResponse(StockMovement movement) {
        String userName = "Unknown";
        if (movement.getUser() != null) {
            String name = movement.getUser().getName() != null ? movement.getUser().getName() : "";
            userName = name.trim();
            if (userName.isEmpty()) {
                userName = movement.getUser().getUsername();
            }
        }
        
        return new StockMovementHistoryResponse(
            movement.getStockMovementId(),
            movement.getItem().getItemId(),
            movement.getItem().getName(),
            movement.getItem().getSku(),
            movement.getMovementType().toString(),
            movement.getQuantity(),
            movement.getPreviousStock(),
            movement.getNewStock(),
            movement.getReason(),
            movement.getRecipient(),
            movement.getNotes(),
            movement.getReferenceNumber(),
            userName,
            movement.getUser() != null ? movement.getUser().getEmail() : "Unknown",
            movement.getCreatedAt(),
            movement.getItem().getUnitPrice()
        );
    }
}
