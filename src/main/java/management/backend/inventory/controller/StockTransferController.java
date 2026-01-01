package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateTransferRequest;
import management.backend.inventory.entity.StockTransfer;
import management.backend.inventory.service.StockTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/stock-transfers")
@Validated
@Tag(name = "Stock Transfers", description = "Inter-warehouse stock transfers endpoints")
public class StockTransferController {

    private final StockTransferService stockTransferService;

    public StockTransferController(StockTransferService stockTransferService) {
        this.stockTransferService = stockTransferService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get stock transfers", description = "Retrieve all stock transfers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfers retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<StockTransfer>> getTransfers() {
        return ResponseEntity.ok(stockTransferService.getAllTransfers());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create stock transfer", description = "Create a new stock transfer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transfer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<StockTransfer> createTransfer(@Valid @RequestBody CreateTransferRequest request, Authentication authentication) {
        StockTransfer created = stockTransferService.createTransfer(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
