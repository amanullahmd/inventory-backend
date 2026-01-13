package management.backend.inventory.controller;

import management.backend.inventory.dto.*;
import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.service.StockOutReasonService;
import management.backend.inventory.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StockController
 * Tests stock movement endpoints
 */
@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockService stockService;

    @Mock
    private StockOutReasonService stockOutReasonService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StockController stockController;

    private StockMovement testMovement;
    private StockMovementRequest testRequest;
    private ItemStockResponse testItemStock;

    @BeforeEach
    void setUp() {
        testMovement = new StockMovement();
        testMovement.setStockMovementId(1L);
        testMovement.setReferenceNumber("REF001");
        testMovement.setQuantity(10L);
        testMovement.setMovementType(MovementType.IN);
        testMovement.setCreatedAt(LocalDateTime.now());

        testRequest = new StockMovementRequest();
        testRequest.setItemId(1L);
        testRequest.setQuantity(10L);

        testItemStock = new ItemStockResponse();
        testItemStock.setItemId(1L);
        testItemStock.setName("Test Item");
        testItemStock.setCurrentStock(100);
    }

    @Test
    @DisplayName("Record stock in creates movement successfully")
    void recordStockIn_CreatesMovement_Successfully() {
        // Arrange
        when(stockService.recordStockIn(any(StockMovementRequest.class), any(Authentication.class)))
            .thenReturn(testMovement);

        // Act
        ResponseEntity<Map<String, Object>> response = stockController.recordStockIn(testRequest, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("id"));
        assertEquals("REF001", response.getBody().get("referenceNumber"));
        verify(stockService).recordStockIn(any(StockMovementRequest.class), any(Authentication.class));
    }

    @Test
    @DisplayName("Record stock in returns bad request for invalid data")
    void recordStockIn_ReturnsBadRequest_ForInvalidData() {
        // Arrange
        when(stockService.recordStockIn(any(StockMovementRequest.class), any(Authentication.class)))
            .thenThrow(new IllegalArgumentException("Invalid item"));

        // Act
        ResponseEntity<Map<String, Object>> response = stockController.recordStockIn(testRequest, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    @DisplayName("Record stock out creates movement successfully")
    void recordStockOut_CreatesMovement_Successfully() {
        // Arrange
        when(stockService.recordStockOut(any(StockMovementRequest.class), any(Authentication.class)))
            .thenReturn(testMovement);

        // Act
        ResponseEntity<StockMovement> response = stockController.recordStockOut(testRequest, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getStockMovementId());
        verify(stockService).recordStockOut(any(StockMovementRequest.class), any(Authentication.class));
    }

    @Test
    @DisplayName("Record stock in batch creates multiple movements")
    void recordStockInBatch_CreatesMultipleMovements() {
        // Arrange
        StockInBatchRequest batchRequest = new StockInBatchRequest();
        batchRequest.setWarehouseId(1L);
        
        StockInBatchRequest.StockInLine line1 = new StockInBatchRequest.StockInLine();
        line1.setItemId(1L);
        line1.setQuantity(10L);
        
        StockInBatchRequest.StockInLine line2 = new StockInBatchRequest.StockInLine();
        line2.setItemId(2L);
        line2.setQuantity(20L);
        
        batchRequest.setItems(Arrays.asList(line1, line2));
        
        List<StockMovement> movements = Arrays.asList(testMovement, testMovement);
        when(stockService.recordStockInBatch(any(StockInBatchRequest.class), any(Authentication.class)))
            .thenReturn(movements);

        // Act
        ResponseEntity<Map<String, Object>> response = stockController.recordStockInBatch(batchRequest, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REF001", response.getBody().get("referenceNumber"));
        assertEquals(2, response.getBody().get("count"));
    }

    @Test
    @DisplayName("Get stock in groups returns grouped summaries")
    void getStockInGroups_ReturnsGroupedSummaries() {
        // Arrange
        List<Map<String, Object>> summaries = Arrays.asList(
            Map.of("referenceNumber", "REF001", "count", 5),
            Map.of("referenceNumber", "REF002", "count", 3)
        );
        when(stockService.getStockInSummaries()).thenReturn(summaries);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = stockController.getStockInGroups();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Get stock in by reference returns details")
    void getStockInByReference_ReturnsDetails() {
        // Arrange
        List<Map<String, Object>> details = Arrays.asList(
            Map.of("itemId", 1L, "quantity", 10)
        );
        when(stockService.getStockInDetails("REF001")).thenReturn(details);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = stockController.getStockInByReference("REF001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Update stock in updates successfully")
    void updateStockIn_UpdatesSuccessfully() {
        // Arrange
        StockInBatchRequest updateRequest = new StockInBatchRequest();
        updateRequest.setWarehouseId(1L);
        
        doNothing().when(stockService).updateStockIn(anyString(), any(StockInBatchRequest.class), any(Authentication.class));

        // Act
        ResponseEntity<Map<String, Object>> response = stockController.updateStockIn("REF001", updateRequest, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REF001", response.getBody().get("referenceNumber"));
    }

    @Test
    @DisplayName("Delete stock in deletes successfully")
    void deleteStockIn_DeletesSuccessfully() {
        // Arrange
        doNothing().when(stockService).deleteStockIn("REF001");

        // Act
        ResponseEntity<Void> response = stockController.deleteStockIn("REF001");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stockService).deleteStockIn("REF001");
    }

    @Test
    @DisplayName("Get stock summary returns all items stock")
    void getStockSummary_ReturnsAllItemsStock() {
        // Arrange
        List<ItemStockResponse> stockList = Arrays.asList(testItemStock);
        when(stockService.getStockSummaryForAllItems()).thenReturn(stockList);

        // Act
        ResponseEntity<List<ItemStockResponse>> response = stockController.getStockSummary();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Get stock summary for item returns item stock")
    void getStockSummaryForItem_ReturnsItemStock() {
        // Arrange
        when(stockService.getStockSummaryForItem(1L)).thenReturn(Optional.of(testItemStock));

        // Act
        ResponseEntity<ItemStockResponse> response = stockController.getStockSummaryForItem(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getItemId());
    }

    @Test
    @DisplayName("Get stock summary for item returns 404 when not found")
    void getStockSummaryForItem_ReturnsNotFound_WhenNotFound() {
        // Arrange
        when(stockService.getStockSummaryForItem(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ItemStockResponse> response = stockController.getStockSummaryForItem(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get predefined reasons returns reasons list")
    void getPredefinedReasons_ReturnsReasonsList() {
        // Arrange
        List<StockOutReasonResponse> reasons = Arrays.asList(
            new StockOutReasonResponse("SOLD", "Sold to customer", 10),
            new StockOutReasonResponse("DAMAGED", "Damaged goods", 5)
        );
        when(stockOutReasonService.getPredefinedReasons()).thenReturn(reasons);

        // Act
        ResponseEntity<List<StockOutReasonResponse>> response = stockController.getPredefinedReasons();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Get reason breakdown returns breakdown report")
    void getReasonBreakdown_ReturnsBreakdownReport() {
        // Arrange
        List<StockOutReasonResponse> reasons = Arrays.asList(
            new StockOutReasonResponse("SOLD", "Sold", 10)
        );
        when(stockOutReasonService.getReasonBreakdown(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(reasons);

        // Act
        ResponseEntity<ReasonBreakdownResponse> response = stockController.getReasonBreakdown(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getTotalMovements());
    }

    @Test
    @DisplayName("Get movement history returns movements")
    void getMovementHistory_ReturnsMovements() {
        // Arrange
        List<StockMovement> movements = Arrays.asList(testMovement);
        when(stockService.getRecentStockMovements()).thenReturn(movements);

        // Act
        ResponseEntity<List<StockMovement>> response = stockController.getMovementHistory(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
