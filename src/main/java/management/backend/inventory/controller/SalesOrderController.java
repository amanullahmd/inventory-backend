package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateSalesOrderRequest;
import management.backend.inventory.entity.SalesOrder;
import management.backend.inventory.service.SalesOrderService;
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
@RequestMapping("/sales-orders")
@Validated
@Tag(name = "Sales Orders", description = "Sales order endpoints")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get sales orders", description = "Retrieve all sales orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sales orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SalesOrder>> getSalesOrders() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create sales order", description = "Create a new sales order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sales order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SalesOrder> createSalesOrder(@Valid @RequestBody CreateSalesOrderRequest request, Authentication authentication) {
        SalesOrder created = salesOrderService.createSalesOrder(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
