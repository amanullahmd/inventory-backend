package management.backend.inventory.controller;

import management.backend.inventory.dto.CategoryResponse;
import management.backend.inventory.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryController
 * Tests category management endpoints
 */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new CategoryResponse();
        testCategory.setCategoryId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic items");
        testCategory.setColor("#3B82F6");
        testCategory.setCategoryCode("ELEC");
        testCategory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Get all categories returns list of categories")
    void getAllCategories_ReturnsCategoryList() {
        // Arrange
        List<CategoryResponse> categories = Arrays.asList(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Electronics", response.getBody().get(0).getName());
        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("Get all categories returns empty list when no categories")
    void getAllCategories_ReturnsEmptyList_WhenNoCategories() {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Get category by ID returns category when found")
    void getCategory_ReturnsCategory_WhenFound() {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.getCategory(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getCategoryId());
        assertEquals("Electronics", response.getBody().getName());
        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @DisplayName("Get category by ID returns 404 when not found")
    void getCategory_ReturnsNotFound_WhenNotFound() {
        // Arrange
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.getCategory(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Create category returns created category")
    void createCategory_ReturnsCreatedCategory() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "New Category");
        request.put("description", "New description");
        request.put("code", "NEW");
        
        when(categoryService.createCategory(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(testCategory);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.createCategory(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(categoryService).createCategory(eq("New Category"), eq("New description"), anyString(), eq("NEW"));
    }

    @Test
    @DisplayName("Create category returns bad request when name is missing")
    void createCategory_ReturnsBadRequest_WhenNameMissing() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("description", "Description only");

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.createCategory(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(categoryService, never()).createCategory(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Create category returns bad request when name is blank")
    void createCategory_ReturnsBadRequest_WhenNameBlank() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "   ");
        request.put("description", "Description");

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.createCategory(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Create category uses default color when not provided")
    void createCategory_UsesDefaultColor_WhenNotProvided() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "New Category");
        request.put("description", "Description");
        
        when(categoryService.createCategory(anyString(), anyString(), eq("#3B82F6"), isNull()))
            .thenReturn(testCategory);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.createCategory(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(categoryService).createCategory(eq("New Category"), eq("Description"), eq("#3B82F6"), isNull());
    }

    @Test
    @DisplayName("Update category returns updated category when found")
    void updateCategory_ReturnsUpdatedCategory_WhenFound() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "Updated Category");
        request.put("description", "Updated description");
        request.put("code", "UPD");
        
        when(categoryService.updateCategory(eq(1L), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(testCategory);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.updateCategory(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(categoryService).updateCategory(eq(1L), eq("Updated Category"), eq("Updated description"), anyString(), eq("UPD"));
    }

    @Test
    @DisplayName("Update category returns 404 when not found")
    void updateCategory_ReturnsNotFound_WhenNotFound() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "Updated Category");
        request.put("description", "Updated description");
        
        when(categoryService.updateCategory(eq(999L), anyString(), anyString(), anyString(), isNull()))
            .thenThrow(new IllegalArgumentException("Category not found"));

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.updateCategory(999L, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Update category returns bad request when name is missing")
    void updateCategory_ReturnsBadRequest_WhenNameMissing() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("description", "Description only");

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.updateCategory(1L, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(categoryService, never()).updateCategory(anyLong(), anyString(), anyString(), anyString(), anyString());
    }
}
