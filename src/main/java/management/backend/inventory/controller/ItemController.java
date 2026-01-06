package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateItemRequest;
import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StatisticsResponse;
import management.backend.inventory.entity.Item;
import management.backend.inventory.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for item management endpoints.
 * Requirements: 8.2, 8.3 - Item creation and retrieval with stock calculations
 * Feature: inventory-backend-oauth2
 */
@RestController
@RequestMapping("/items")
@Validated
@Tag(name = "Items", description = "Item management endpoints")
public class ItemController {
    
    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    /**
     * GET /api/items - Retrieve all items with current stock calculations.
     * Requirements: 8.2, 8.3 - Item retrieval with stock calculations
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ITEM_READ')")
    @Operation(summary = "Get all items", description = "Retrieve all inventory items with current stock calculations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<ItemStockResponse>> getAllItems() {
        List<ItemStockResponse> items = itemService.getAllItemsWithStock();
        return ResponseEntity.ok(items);
    }
    
    /**
     * POST /api/items - Create a new inventory item.
     * Requirements: 8.2, 8.3 - Item creation with validation
     * Accessible to authenticated users (both Admin and User roles)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ITEM_CREATE')")
    @Operation(summary = "Create new item", description = "Create a new inventory item with name, SKU, and unit cost")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ItemStockResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        Item createdItem = itemService.createItem(request);
        
        // Get the item with stock information
        ItemStockResponse response = itemService.getItemWithStock(createdItem.getItemId())
            .orElseThrow(() -> new RuntimeException("Failed to retrieve created item"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * GET /api/items/{itemId} - Retrieve a specific item with stock information.
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ITEM_READ')")
    @Operation(summary = "Get item by ID", description = "Retrieve a specific item with its current stock information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ItemStockResponse> getItem(@PathVariable Long itemId) {
        return itemService.getItemWithStock(itemId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/items/statistics - Retrieve dashboard statistics.
     * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5 - Dashboard statistics calculation
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get statistics", description = "Retrieve dashboard statistics including total items, stock in/out counts, and total value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse statistics = itemService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * PUT /api/items/{itemId} - Update an existing item.
     * Accessible to authenticated users.
     */
    @PutMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ITEM_UPDATE')")
    @Operation(summary = "Update item", description = "Update item details like name, SKU, unit price, description, category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ItemStockResponse> updateItem(@PathVariable Long itemId, @Valid @RequestBody CreateItemRequest request) {
        if (!itemService.existsById(itemId)) {
            return ResponseEntity.notFound().build();
        }

        Item updated = itemService.updateItem(itemId, request);
        ItemStockResponse response = itemService.getItemWithStock(updated.getItemId())
            .orElseThrow(() -> new RuntimeException("Failed to retrieve updated item"));
        return ResponseEntity.ok(response);
    }
}
