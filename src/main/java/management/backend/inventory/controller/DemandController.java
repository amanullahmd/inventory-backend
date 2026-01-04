package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateDemandRequest;
import management.backend.inventory.dto.DemandResponse;
import management.backend.inventory.service.DemandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/demands")
@Validated
@Tag(name = "Demands")
public class DemandController {
    private final DemandService demandService;
    public DemandController(DemandService demandService) { this.demandService = demandService; }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List demands")
    public ResponseEntity<List<DemandResponse>> list() {
        return ResponseEntity.ok(demandService.list());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get demand")
    public ResponseEntity<DemandResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(demandService.get(id));
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create demand")
    public ResponseEntity<DemandResponse> create(@Valid @RequestBody CreateDemandRequest request, Authentication authentication) {
        DemandResponse created = demandService.create(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update demand")
    public ResponseEntity<DemandResponse> update(@PathVariable Long id, @RequestBody management.backend.inventory.dto.UpdateDemandRequest request) {
        return ResponseEntity.ok(demandService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete demand")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        demandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
