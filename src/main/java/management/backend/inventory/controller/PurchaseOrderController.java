package management.backend.inventory.controller;

import management.backend.inventory.dto.CreatePurchaseOrderRequest;
import management.backend.inventory.entity.PurchaseOrder;
import management.backend.inventory.dto.AddPurchaseOrderItemsRequest;
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
    public ResponseEntity<List<management.backend.inventory.dto.PurchaseOrderResponse>> getPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get purchase order detail", description = "Retrieve purchase order with item lines")
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderDetailResponse> getPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrder(id));
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
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderResponse> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request, Authentication authentication) {
        PurchaseOrder created = purchaseOrderService.createPurchaseOrder(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.toResponse(created));
    }
    
    @PostMapping("/{id}/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add items to purchase order", description = "Append item lines with unit price and quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderResponse> addItems(@PathVariable Long id, @Valid @RequestBody AddPurchaseOrderItemsRequest request) {
        PurchaseOrder updated = purchaseOrderService.addItems(id, request);
        return ResponseEntity.ok(purchaseOrderService.toResponse(updated));
    }
    
    @PutMapping("/{id}/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Replace items of purchase order", description = "Replace item lines and recompute totals")
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderResponse> replaceItems(@PathVariable Long id, @Valid @RequestBody AddPurchaseOrderItemsRequest request) {
        PurchaseOrder updated = purchaseOrderService.replaceItems(id, request);
        return ResponseEntity.ok(purchaseOrderService.toResponse(updated));
    }
    
    @DeleteMapping("/items/{purchaseOrderItemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete purchase order item", description = "Delete a specific item line and recompute totals")
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderResponse> deleteItem(@PathVariable Long purchaseOrderItemId) {
        PurchaseOrder updated = purchaseOrderService.deleteItem(purchaseOrderItemId);
        return ResponseEntity.ok(purchaseOrderService.toResponse(updated));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update purchase order", description = "Update header fields and status")
    public ResponseEntity<management.backend.inventory.dto.PurchaseOrderResponse> updatePurchaseOrder(@PathVariable Long id, @RequestBody management.backend.inventory.dto.UpdatePurchaseOrderRequest request) {
        PurchaseOrder updated = purchaseOrderService.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(purchaseOrderService.toResponse(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete purchase order", description = "Delete only DRAFT orders")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
