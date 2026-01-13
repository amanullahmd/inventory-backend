package management.backend.inventory.controller;

import management.backend.inventory.dto.CreateItemRequest;
import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StatisticsResponse;
import management.backend.inventory.entity.Item;
import management.backend.inventory.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ItemController
 * Tests item management endpoints
 */
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemStockResponse testItemResponse;
    private CreateItemRequest testCreateRequest;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testItemResponse = new ItemStockResponse();
        testItemResponse.setItemId(1L);
        testItemResponse.setName("Test Item");
        testItemResponse.setSku("SKU001");
        testItemResponse.setUnitPrice(BigDecimal.valueOf(10.00));
        testItemResponse.setCurrentStock(100);
        testItemResponse.setCreatedAt(LocalDateTime.now());

        testCreateRequest = new CreateItemRequest();
        testCreateRequest.setName("New Item");
        testCreateRequest.setSku("SKU002");
        testCreateRequest.setUnitPrice(BigDecimal.valueOf(15.00));
        testCreateRequest.setCategoryId(1L);

        testItem = new Item();
        testItem.setItemId(1L);
        testItem.setName("Test Item");
        testItem.setSku("SKU001");
    }

    @Test
    @DisplayName("Get all items returns list of items")
    void getAllItems_ReturnsItemList() {
        // Arrange
        List<ItemStockResponse> items = Arrays.asList(testItemResponse);
        when(itemService.getAllItemsWithStock()).thenReturn(items);

        // Act
        ResponseEntity<List<ItemStockResponse>> response = itemController.getAllItems();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Item", response.getBody().get(0).getName());
        verify(itemService).getAllItemsWithStock();
    }

    @Test
    @DisplayName("Get all items returns empty list when no items")
    void getAllItems_ReturnsEmptyList_WhenNoItems() {
        // Arrange
        when(itemService.getAllItemsWithStock()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<ItemStockResponse>> response = itemController.getAllItems();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Create item returns created item")
    void createItem_ReturnsCreatedItem() {
        // Arrange
        when(itemService.createItem(any(CreateItemRequest.class))).thenReturn(testItem);
        when(itemService.getItemWithStock(anyLong())).thenReturn(Optional.of(testItemResponse));

        // Act
        ResponseEntity<ItemStockResponse> response = itemController.createItem(testCreateRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Item", response.getBody().getName());
        verify(itemService).createItem(any(CreateItemRequest.class));
        verify(itemService).getItemWithStock(anyLong());
    }

    @Test
    @DisplayName("Get item by ID returns item when found")
    void getItem_ReturnsItem_WhenFound() {
        // Arrange
        when(itemService.getItemWithStock(1L)).thenReturn(Optional.of(testItemResponse));

        // Act
        ResponseEntity<ItemStockResponse> response = itemController.getItem(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getItemId());
        verify(itemService).getItemWithStock(1L);
    }

    @Test
    @DisplayName("Get item by ID returns 404 when not found")
    void getItem_ReturnsNotFound_WhenNotFound() {
        // Arrange
        when(itemService.getItemWithStock(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ItemStockResponse> response = itemController.getItem(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get statistics returns statistics response")
    void getStatistics_ReturnsStatistics() {
        // Arrange
        StatisticsResponse stats = new StatisticsResponse();
        stats.setTotalItems(10);
        stats.setTotalValue(BigDecimal.valueOf(1000.00));
        stats.setLowStockItems(2);
        stats.setOutOfStockItems(1);
        when(itemService.getStatistics()).thenReturn(stats);

        // Act
        ResponseEntity<StatisticsResponse> response = itemController.getStatistics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getTotalItems());
        assertEquals(BigDecimal.valueOf(1000.00), response.getBody().getTotalValue());
        verify(itemService).getStatistics();
    }

    @Test
    @DisplayName("Update item returns updated item when found")
    void updateItem_ReturnsUpdatedItem_WhenFound() {
        // Arrange
        when(itemService.existsById(1L)).thenReturn(true);
        when(itemService.updateItem(anyLong(), any(CreateItemRequest.class))).thenReturn(testItem);
        when(itemService.getItemWithStock(anyLong())).thenReturn(Optional.of(testItemResponse));

        // Act
        ResponseEntity<ItemStockResponse> response = itemController.updateItem(1L, testCreateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(itemService).existsById(1L);
        verify(itemService).updateItem(anyLong(), any(CreateItemRequest.class));
    }

    @Test
    @DisplayName("Update item returns 404 when not found")
    void updateItem_ReturnsNotFound_WhenNotFound() {
        // Arrange
        when(itemService.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<ItemStockResponse> response = itemController.updateItem(999L, testCreateRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemService).existsById(999L);
        verify(itemService, never()).updateItem(anyLong(), any(CreateItemRequest.class));
    }
}
