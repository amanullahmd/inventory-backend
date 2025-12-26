package management.backend.inventory.service;

import management.backend.inventory.dto.StockOutReasonResponse;
import management.backend.inventory.entity.StockOutReasonEnum;
import management.backend.inventory.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StockOutReasonService.
 * Tests cover reason management, breakdown calculations, and validation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StockOutReasonService Tests")
class StockOutReasonServiceTest {
    
    @Mock
    private StockMovementRepository stockMovementRepository;
    
    @InjectMocks
    private StockOutReasonService stockOutReasonService;
    
    private LocalDate testStartDate;
    private LocalDate testEndDate;
    private LocalDateTime testStartDateTime;
    private LocalDateTime testEndDateTime;
    
    @BeforeEach
    void setUp() {
        testStartDate = LocalDate.of(2024, 1, 1);
        testEndDate = LocalDate.of(2024, 12, 31);
        testStartDateTime = testStartDate.atStartOfDay();
        testEndDateTime = testEndDate.atTime(LocalTime.MAX);
    }
    
    // ==================== Predefined Reasons Tests ====================
    
    @Test
    @DisplayName("Should get all predefined reasons")
    void testGetPredefinedReasons() {
        // Act
        List<StockOutReasonResponse> reasons = stockOutReasonService.getPredefinedReasons();
        
        // Assert
        assertNotNull(reasons);
        assertEquals(7, reasons.size());
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("TRANSFERRED")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("GIVEN")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("EXPIRED")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("LOST")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("USED")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("DAMAGED")));
        assertTrue(reasons.stream().anyMatch(r -> r.getReasonType().equals("OTHER")));
    }
    
    @Test
    @DisplayName("Should have non-null labels for all predefined reasons")
    void testPredefinedReasonsHaveLabels() {
        // Act
        List<StockOutReasonResponse> reasons = stockOutReasonService.getPredefinedReasons();
        
        // Assert
        reasons.forEach(reason -> {
            assertNotNull(reason.getReasonLabel());
            assertFalse(reason.getReasonLabel().isBlank());
        });
    }
    
    // ==================== Reason Breakdown Tests ====================
    
    @Test
    @DisplayName("Should calculate reason breakdown with counts and percentages")
    void testGetReasonBreakdown() {
        // Arrange
        List<StockMovementRepository.ReasonBreakdownProjection> mockProjections = 
            createMockReasonBreakdownProjections();
        when(stockMovementRepository.getReasonBreakdown(testStartDateTime, testEndDateTime))
            .thenReturn(mockProjections);
        
        // Act
        List<StockOutReasonResponse> breakdown = stockOutReasonService.getReasonBreakdown(testStartDate, testEndDate);
        
        // Assert
        assertNotNull(breakdown);
        assertEquals(3, breakdown.size());
        
        // Verify counts
        StockOutReasonResponse transferred = breakdown.stream()
            .filter(r -> r.getReasonType().equals("TRANSFERRED"))
            .findFirst()
            .orElse(null);
        assertNotNull(transferred);
        assertEquals(50, transferred.getCount());
        assertEquals(50.0, transferred.getPercentage(), 0.01);
        
        // Verify total percentage is 100%
        double totalPercentage = breakdown.stream()
            .mapToDouble(StockOutReasonResponse::getPercentage)
            .sum();
        assertEquals(100.0, totalPercentage, 0.01);
    }
    
    @Test
    @DisplayName("Should handle empty reason breakdown")
    void testGetReasonBreakdownEmpty() {
        // Arrange
        when(stockMovementRepository.getReasonBreakdown(testStartDateTime, testEndDateTime))
            .thenReturn(Collections.emptyList());
        
        // Act
        List<StockOutReasonResponse> breakdown = stockOutReasonService.getReasonBreakdown(testStartDate, testEndDate);
        
        // Assert
        assertNotNull(breakdown);
        assertTrue(breakdown.isEmpty());
    }
    
    @Test
    @DisplayName("Should get reason breakdown for specific item")
    void testGetReasonBreakdownByItem() {
        // Arrange
        Long itemId = 1L;
        List<StockMovementRepository.ReasonBreakdownProjection> mockProjections = 
            createMockReasonBreakdownProjections();
        when(stockMovementRepository.getReasonBreakdownByItem(itemId, testStartDateTime, testEndDateTime))
            .thenReturn(mockProjections);
        
        // Act
        List<StockOutReasonResponse> breakdown = stockOutReasonService.getReasonBreakdownByItem(itemId, testStartDate, testEndDate);
        
        // Assert
        assertNotNull(breakdown);
        assertEquals(3, breakdown.size());
        verify(stockMovementRepository).getReasonBreakdownByItem(itemId, testStartDateTime, testEndDateTime);
    }
    
    @Test
    @DisplayName("Should get reason breakdown for specific category")
    void testGetReasonBreakdownByCategory() {
        // Arrange
        Long categoryId = 1L;
        List<StockMovementRepository.ReasonBreakdownProjection> mockProjections = 
            createMockReasonBreakdownProjections();
        when(stockMovementRepository.getReasonBreakdownByCategory(categoryId, testStartDateTime, testEndDateTime))
            .thenReturn(mockProjections);
        
        // Act
        List<StockOutReasonResponse> breakdown = stockOutReasonService.getReasonBreakdownByCategory(categoryId, testStartDate, testEndDate);
        
        // Assert
        assertNotNull(breakdown);
        assertEquals(3, breakdown.size());
        verify(stockMovementRepository).getReasonBreakdownByCategory(categoryId, testStartDateTime, testEndDateTime);
    }
    
    // ==================== Reason Counts Tests ====================
    
    @Test
    @DisplayName("Should get reason counts for all reasons")
    void testGetReasonCounts() {
        // Arrange
        when(stockMovementRepository.countByReasonType("TRANSFERRED")).thenReturn(50);
        when(stockMovementRepository.countByReasonType("GIVEN")).thenReturn(30);
        when(stockMovementRepository.countByReasonType("EXPIRED")).thenReturn(20);
        when(stockMovementRepository.countByReasonType("LOST")).thenReturn(0);
        when(stockMovementRepository.countByReasonType("USED")).thenReturn(0);
        when(stockMovementRepository.countByReasonType("DAMAGED")).thenReturn(0);
        when(stockMovementRepository.countByReasonType("OTHER")).thenReturn(0);
        
        // Act
        Map<String, Integer> counts = stockOutReasonService.getReasonCounts();
        
        // Assert
        assertNotNull(counts);
        assertEquals(7, counts.size());
        assertEquals(50, counts.get("TRANSFERRED"));
        assertEquals(30, counts.get("GIVEN"));
        assertEquals(20, counts.get("EXPIRED"));
    }
    
    @Test
    @DisplayName("Should handle null counts from repository")
    void testGetReasonCountsWithNullValues() {
        // Arrange
        when(stockMovementRepository.countByReasonType(anyString())).thenReturn(null);
        
        // Act
        Map<String, Integer> counts = stockOutReasonService.getReasonCounts();
        
        // Assert
        assertNotNull(counts);
        counts.values().forEach(count -> assertEquals(0, count));
    }
    
    // ==================== Reason Validation Tests ====================
    
    @Test
    @DisplayName("Should validate predefined reason")
    void testValidatePredefinedReason() {
        // Act & Assert
        assertTrue(stockOutReasonService.validateReason("TRANSFERRED"));
        assertTrue(stockOutReasonService.validateReason("GIVEN"));
        assertTrue(stockOutReasonService.validateReason("EXPIRED"));
        assertTrue(stockOutReasonService.validateReason("LOST"));
        assertTrue(stockOutReasonService.validateReason("USED"));
        assertTrue(stockOutReasonService.validateReason("DAMAGED"));
        assertTrue(stockOutReasonService.validateReason("OTHER"));
    }
    
    @Test
    @DisplayName("Should validate custom reason")
    void testValidateCustomReason() {
        // Act & Assert
        assertTrue(stockOutReasonService.validateReason("Custom reason"));
        assertTrue(stockOutReasonService.validateReason("Another custom reason"));
    }
    
    @Test
    @DisplayName("Should reject null reason")
    void testValidateNullReason() {
        // Act & Assert
        assertFalse(stockOutReasonService.validateReason(null));
    }
    
    @Test
    @DisplayName("Should reject blank reason")
    void testValidateBlankReason() {
        // Act & Assert
        assertFalse(stockOutReasonService.validateReason(""));
        assertFalse(stockOutReasonService.validateReason("   "));
    }
    
    @Test
    @DisplayName("Should reject reason exceeding max length")
    void testValidateReasonExceedingMaxLength() {
        // Arrange
        String longReason = "a".repeat(101);
        
        // Act & Assert
        assertFalse(stockOutReasonService.validateReason(longReason));
    }
    
    @Test
    @DisplayName("Should accept reason at max length")
    void testValidateReasonAtMaxLength() {
        // Arrange
        String maxLengthReason = "a".repeat(100);
        
        // Act & Assert
        assertTrue(stockOutReasonService.validateReason(maxLengthReason));
    }
    
    // ==================== Reason Type Validation Tests ====================
    
    @Test
    @DisplayName("Should validate reason type enum")
    void testValidateReasonType() {
        // Act & Assert
        assertTrue(stockOutReasonService.validateReasonType("TRANSFERRED"));
        assertTrue(stockOutReasonService.validateReasonType("GIVEN"));
        assertTrue(stockOutReasonService.validateReasonType("OTHER"));
    }
    
    @Test
    @DisplayName("Should reject invalid reason type")
    void testValidateInvalidReasonType() {
        // Act & Assert
        assertFalse(stockOutReasonService.validateReasonType("INVALID"));
        assertFalse(stockOutReasonService.validateReasonType(""));
        assertFalse(stockOutReasonService.validateReasonType(null));
    }
    
    // ==================== Reason Type Enum Tests ====================
    
    @Test
    @DisplayName("Should get reason type enum from string")
    void testGetReasonTypeEnum() {
        // Act
        StockOutReasonEnum transferred = stockOutReasonService.getReasonTypeEnum("TRANSFERRED");
        StockOutReasonEnum given = stockOutReasonService.getReasonTypeEnum("GIVEN");
        
        // Assert
        assertNotNull(transferred);
        assertEquals(StockOutReasonEnum.TRANSFERRED, transferred);
        assertNotNull(given);
        assertEquals(StockOutReasonEnum.GIVEN, given);
    }
    
    @Test
    @DisplayName("Should return null for invalid reason type enum")
    void testGetReasonTypeEnumInvalid() {
        // Act
        StockOutReasonEnum result = stockOutReasonService.getReasonTypeEnum("INVALID");
        
        // Assert
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should return null for null reason type")
    void testGetReasonTypeEnumNull() {
        // Act
        StockOutReasonEnum result = stockOutReasonService.getReasonTypeEnum(null);
        
        // Assert
        assertNull(result);
    }
    
    // ==================== Top Reasons Tests ====================
    
    @Test
    @DisplayName("Should get top reasons")
    void testGetTopReasons() {
        // Arrange
        List<StockMovementRepository.ReasonBreakdownProjection> mockProjections = 
            createMockReasonBreakdownProjections();
        when(stockMovementRepository.getTopReasons(5))
            .thenReturn(mockProjections);
        
        // Act
        List<StockOutReasonResponse> topReasons = stockOutReasonService.getTopReasons(5);
        
        // Assert
        assertNotNull(topReasons);
        assertEquals(3, topReasons.size());
        verify(stockMovementRepository).getTopReasons(5);
    }
    
    @Test
    @DisplayName("Should handle empty top reasons")
    void testGetTopReasonsEmpty() {
        // Arrange
        when(stockMovementRepository.getTopReasons(5))
            .thenReturn(Collections.emptyList());
        
        // Act
        List<StockOutReasonResponse> topReasons = stockOutReasonService.getTopReasons(5);
        
        // Assert
        assertNotNull(topReasons);
        assertTrue(topReasons.isEmpty());
    }
    
    // ==================== Reasons by Date Tests ====================
    
    @Test
    @DisplayName("Should get reasons for specific date")
    void testGetReasonsByDate() {
        // Arrange
        LocalDate specificDate = LocalDate.of(2024, 6, 15);
        List<StockMovementRepository.ReasonBreakdownProjection> mockProjections = 
            createMockReasonBreakdownProjections();
        when(stockMovementRepository.getReasonBreakdown(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(mockProjections);
        
        // Act
        List<StockOutReasonResponse> reasons = stockOutReasonService.getReasonsByDate(specificDate);
        
        // Assert
        assertNotNull(reasons);
        assertEquals(3, reasons.size());
    }
    
    // ==================== Helper Methods ====================
    
    private List<StockMovementRepository.ReasonBreakdownProjection> createMockReasonBreakdownProjections() {
        List<StockMovementRepository.ReasonBreakdownProjection> projections = new ArrayList<>();
        
        projections.add(new StockMovementRepository.ReasonBreakdownProjection() {
            @Override
            public String getReasonType() {
                return "TRANSFERRED";
            }
            
            @Override
            public Integer getCount() {
                return 50;
            }
        });
        
        projections.add(new StockMovementRepository.ReasonBreakdownProjection() {
            @Override
            public String getReasonType() {
                return "GIVEN";
            }
            
            @Override
            public Integer getCount() {
                return 30;
            }
        });
        
        projections.add(new StockMovementRepository.ReasonBreakdownProjection() {
            @Override
            public String getReasonType() {
                return "EXPIRED";
            }
            
            @Override
            public Integer getCount() {
                return 20;
            }
        });
        
        return projections;
    }
}
