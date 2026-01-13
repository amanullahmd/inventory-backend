package management.backend.inventory.service;

import management.backend.inventory.dto.CategoryResponse;
import management.backend.inventory.entity.Category;
import management.backend.inventory.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService
 * Tests category management business logic
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic items");
        testCategory.setColor("#3B82F6");
        testCategory.setCategoryCode("ELEC");
        testCategory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Get all categories returns list")
    void getAllCategories_ReturnsList() {
        // Arrange
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList(testCategory));

        // Act
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryRepository).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Get all categories returns empty list when no categories")
    void getAllCategories_ReturnsEmptyList_WhenNoCategories() {
        // Arrange
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(Arrays.asList());

        // Act
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get category by ID returns category when found")
    void getCategoryById_ReturnsCategory_WhenFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<CategoryResponse> result = categoryService.getCategoryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        assertEquals(1L, result.get().getCategoryId());
    }

    @Test
    @DisplayName("Get category by ID returns empty when not found")
    void getCategoryById_ReturnsEmpty_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<CategoryResponse> result = categoryService.getCategoryById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Create category successfully")
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsByNameIgnoreCase("New Category")).thenReturn(false);
        when(categoryRepository.existsByCategoryCode("NEW")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            cat.setCategoryId(2L);
            cat.setCreatedAt(LocalDateTime.now());
            return cat;
        });

        // Act
        CategoryResponse result = categoryService.createCategory("New Category", "Description", "#FF0000", "NEW");

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals("#FF0000", result.getColor());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Create category fails when name already exists")
    void createCategory_FailsWhenNameExists() {
        // Arrange
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> categoryService.createCategory("Electronics", "Description", "#FF0000", "ELEC"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Update category successfully")
    void updateCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByCategoryCode("UPD")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, "Updated Name", "Updated Desc", "#00FF00", "UPD");

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Update category fails when not found")
    void updateCategory_FailsWhenNotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> categoryService.updateCategory(999L, "Name", "Desc", "#FF0000", "CODE"));
    }

    @Test
    @DisplayName("Delete category successfully")
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete category fails when not found")
    void deleteCategory_FailsWhenNotFound() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(999L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
