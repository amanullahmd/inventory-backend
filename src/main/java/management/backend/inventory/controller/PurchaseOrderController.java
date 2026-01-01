package management.backend.inventory.controller;

import management.backend.inventory.dto.CreatePurchaseOrderRequest;
import management.backend.inventory.entity.PurchaseOrder;
import management.backend.inventory.service.PurchaseOrderService;
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
@RequestMapping("/purchase-orders")
@Validated
@Tag(name = "Purchase Orders", description = "Purchase order endpoints")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get purchase orders", description = "Retrieve all purchase orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create purchase order", description = "Create a new purchase order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Purchase order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request, Authentication authentication) {
        PurchaseOrder created = purchaseOrderService.createPurchaseOrder(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
