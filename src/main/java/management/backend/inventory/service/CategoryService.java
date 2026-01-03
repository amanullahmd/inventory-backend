package management.backend.inventory.service;

import management.backend.inventory.dto.CategoryResponse;
import management.backend.inventory.entity.Category;
import management.backend.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for category management.
 * Requirements: 3.1, 3.2 - Category management business logic
 */
@Service
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Create a new category.
     * Requirements: 3.1 - Category creation with validation
     */
    public CategoryResponse createCategory(String name, String description, String color, String code) {
        // Validate that category name is unique
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Category code is required");
        }
        if (categoryRepository.existsByCategoryCode(code)) {
            throw new IllegalArgumentException("Category code '" + code + "' already exists");
        }
        
        // Create and save the category
        Category category = new Category(name, description);
        category.setColor(color);
        category.setCategoryCode(code);
        Category savedCategory = categoryRepository.save(category);
        
        return convertToResponse(savedCategory);
    }
    
    /**
     * Get all categories.
     * Requirements: 3.2 - Category retrieval
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get category by ID.
     */
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .map(this::convertToResponse);
    }
    
    /**
     * Get category by name.
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Update a category.
     */
    public CategoryResponse updateCategory(Long categoryId, String name, String description, String color, String code) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));
        
        // Check if new name is unique (if changed)
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Category code is required");
        }
        Optional<Category> existing = categoryRepository.findByCategoryCode(code);
        if (existing.isPresent() && !existing.get().getCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("Category code '" + code + "' already exists");
        }
        category.setCategoryCode(code);
        
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToResponse(updatedCategory);
    }
    
    /**
     * Delete a category.
     */
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }
    
    /**
     * Check if category exists by ID.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
    
    /**
     * Convert Category entity to CategoryResponse DTO.
     */
    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(
            category.getCategoryId(),
            category.getName(),
            category.getDescription(),
            category.getColor(),
            category.getCategoryCode(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
