package management.backend.inventory.controller;

import management.backend.inventory.dto.CategoryResponse;
import management.backend.inventory.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for category management endpoints.
 * Requirements: 3.1, 3.2 - Category creation and retrieval
 * Feature: inventory-backend-oauth2
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * GET /api/categories - Retrieve all categories.
     * Requirements: 3.2 - Category retrieval
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * GET /api/categories/{categoryId} - Retrieve a specific category.
     * Accessible to authenticated users (both Admin and User roles)
     */
    @GetMapping("/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/categories - Create a new category.
     * Requirements: 3.1 - Category creation with validation
     * Accessible to authenticated users (both Admin and User roles)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        String color = request.getOrDefault("color", "#3B82F6"); // Default blue color
        
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            CategoryResponse category = categoryService.createCategory(name, description, color);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PUT /api/categories/{categoryId} - Update a category.
     * Accessible to authenticated users (both Admin and User roles)
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        String color = request.getOrDefault("color", "#3B82F6"); // Default blue color
        
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            CategoryResponse category = categoryService.updateCategory(categoryId, name, description, color);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/categories/{categoryId} - Delete a category.
     * Accessible to authenticated users (both Admin and User roles)
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
