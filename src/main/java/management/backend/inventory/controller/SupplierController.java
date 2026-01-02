package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateSupplierRequest;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.service.SupplierService;
import management.backend.inventory.dto.UpdateSupplierRequest;
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

@RestController
@RequestMapping("/suppliers")
@Validated
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get suppliers", description = "Retrieve all suppliers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<Supplier>> getSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create supplier", description = "Create a new supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier created = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update supplier", description = "Update supplier details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody UpdateSupplierRequest request) {
        Supplier updated = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set supplier active", description = "Activate or deactivate a supplier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Supplier> setActive(@PathVariable Long id, @RequestParam("active") Boolean active) {
        Supplier updated = supplierService.setActive(id, active);
        return ResponseEntity.ok(updated);
    }

    // Deletion disabled by business rule
}
