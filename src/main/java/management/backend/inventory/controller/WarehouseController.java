package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateWarehouseRequest;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.service.WarehouseService;
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
import management.backend.inventory.dto.UpdateWarehouseRequest;

@RestController
@RequestMapping("/warehouses")
@Validated
@Tag(name = "Warehouses", description = "Warehouse management endpoints")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get warehouses", description = "Retrieve all warehouses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<Warehouse>> getWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create warehouse", description = "Create a new warehouse")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        Warehouse created = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update warehouse", description = "Update warehouse details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Warehouse updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long id, @Valid @RequestBody UpdateWarehouseRequest request) {
        Warehouse updated = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set warehouse active", description = "Activate or deactivate a warehouse")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Warehouse status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Warehouse> setActive(@PathVariable Long id, @RequestParam("active") Boolean active) {
        Warehouse updated = warehouseService.setActive(id, active);
        return ResponseEntity.ok(updated);
    }
    
    // Deletion disabled by business rule
}
