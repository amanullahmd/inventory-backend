package management.backend.inventory.service;

import management.backend.inventory.dto.CreateItemRequest;
import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StatisticsResponse;
import management.backend.inventory.entity.Category;
import management.backend.inventory.entity.Item;
import management.backend.inventory.repository.CategoryRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ItemService
 * Tests item management business logic
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private Category testCategory;
    private CreateItemRequest testRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setName("Electronics");

        testItem = new Item();
        testItem.setItemId(1L);
        testItem.setName("Test Item");
        testItem.setSku("SKU001");
        testItem.setUnitPrice(BigDecimal.valueOf(10.00));
        testItem.setCategory(testCategory);
        testItem.setCurrentStock(100L);
        testItem.setMinimumStock(10L);
        testItem.setCreatedAt(LocalDateTime.now());

        testRequest = new CreateItemRequest();
        testRequest.setName("New Item");
        testRequest.setSku("SKU002");
        testRequest.setUnitPrice(BigDecimal.valueOf(15.00));
        testRequest.setCategoryId(1L);
    }

    @Test
    @DisplayName("Create item successfully")
    void createItem_Success() {
        // Arrange
        when(itemRepository.existsBySku("SKU002")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setItemId(2L);
            return item;
        });

        // Act
        Item result = itemService.createItem(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Item", result.getName());
        assertEquals("SKU002", result.getSku());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("Create item fails when SKU already exists")
    void createItem_FailsWhenSkuExists() {
        // Arrange
        when(itemRepository.existsBySku("SKU002")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(testRequest));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Create item fails when category is null")
    void createItem_FailsWhenCategoryNull() {
        // Arrange
        testRequest.setCategoryId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(testRequest));
    }

    @Test
    @DisplayName("Create item fails when category not found")
    void createItem_FailsWhenCategoryNotFound() {
        // Arrange
        when(itemRepository.existsBySku("SKU002")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(testRequest));
    }

    @Test
    @DisplayName("Update item successfully")
    void updateItem_Success() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findBySku("SKU003")).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        CreateItemRequest updateRequest = new CreateItemRequest();
        updateRequest.setName("Updated Item");
        updateRequest.setSku("SKU003");

        // Act
        Item result = itemService.updateItem(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("Update item fails when item not found")
    void updateItem_FailsWhenNotFound() {
        // Arrange
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(999L, testRequest));
    }

    @Test
    @DisplayName("Update item fails when SKU already exists for different item")
    void updateItem_FailsWhenSkuExistsForDifferentItem() {
        // Arrange
        Item otherItem = new Item();
        otherItem.setItemId(2L);
        otherItem.setSku("SKU002");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findBySku("SKU002")).thenReturn(Optional.of(otherItem));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(1L, testRequest));
    }

    @Test
    @DisplayName("Get all items with stock returns list")
    void getAllItemsWithStock_ReturnsList() {
        // Arrange
        when(itemRepository.findAllItemsOrderByCreatedAt()).thenReturn(Arrays.asList(testItem));
        when(stockMovementRepository.getStockSummaryForAllItems()).thenReturn(Arrays.asList());

        // Act
        List<ItemStockResponse> result = itemService.getAllItemsWithStock();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
    }

    @Test
    @DisplayName("Find item by ID returns item when found")
    void findById_ReturnsItem_WhenFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        // Act
        Optional<Item> result = itemService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Item", result.get().getName());
    }

    @Test
    @DisplayName("Find item by ID returns empty when not found")
    void findById_ReturnsEmpty_WhenNotFound() {
        // Arrange
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Item> result = itemService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Find item by ID returns empty when ID is null")
    void findById_ReturnsEmpty_WhenIdNull() {
        // Act
        Optional<Item> result = itemService.findById(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Get item with stock returns response when found")
    void getItemWithStock_ReturnsResponse_WhenFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(stockMovementRepository.getStockSummaryForItem(1L)).thenReturn(null);

        // Act
        Optional<ItemStockResponse> result = itemService.getItemWithStock(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Item", result.get().getName());
    }

    @Test
    @DisplayName("Get item with stock returns empty when not found")
    void getItemWithStock_ReturnsEmpty_WhenNotFound() {
        // Arrange
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ItemStockResponse> result = itemService.getItemWithStock(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Exists by ID returns true when exists")
    void existsById_ReturnsTrue_WhenExists() {
        // Arrange
        when(itemRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = itemService.existsById(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Exists by ID returns false when not exists")
    void existsById_ReturnsFalse_WhenNotExists() {
        // Arrange
        when(itemRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = itemService.existsById(999L);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Exists by ID returns false when ID is null")
    void existsById_ReturnsFalse_WhenIdNull() {
        // Act
        boolean result = itemService.existsById(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Get statistics returns correct values")
    void getStatistics_ReturnsCorrectValues() {
        // Arrange
        when(itemRepository.findAllItemsOrderByCreatedAt()).thenReturn(Arrays.asList(testItem));
        when(stockMovementRepository.getStockSummaryForAllItems()).thenReturn(Arrays.asList());

        // Act
        StatisticsResponse result = itemService.getStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
    }

    @Test
    @DisplayName("Search items by name returns matching items")
    void searchItemsByName_ReturnsMatchingItems() {
        // Arrange
        when(itemRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testItem));

        // Act
        List<Item> result = itemService.searchItemsByName("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
    }
}
