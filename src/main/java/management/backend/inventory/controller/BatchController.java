package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateBatchRequest;
import management.backend.inventory.entity.Batch;
import management.backend.inventory.service.BatchService;
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
@RequestMapping("/batches")
@Validated
@Tag(name = "Batches", description = "Batch management endpoints")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get batches", description = "Retrieve all batches")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batches retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<Batch>> getBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create batch", description = "Create a new batch for an item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Batch created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Batch> createBatch(@Valid @RequestBody CreateBatchRequest request) {
        Batch created = batchService.createBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
