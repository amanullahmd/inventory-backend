package management.backend.inventory.repository;

import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for StockMovement entity operations.
 * Requirements: 4.5, 3.4 - Stock movement repository with aggregation queries for stock calculations
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    /**
     * Find all stock movements for a specific item, ordered by creation date.
     */
    List<StockMovement> findByItemItemIdOrderByCreatedAtDesc(Long itemId);
    
    /**
     * Find stock movements by movement type for a specific item.
     */
    List<StockMovement> findByItemItemIdAndMovementType(Long itemId, MovementType movementType);
    
    /**
     * Calculate total stock IN for a specific item.
     */
    @Query("""
        SELECT COALESCE(SUM(sm.quantity), 0) 
        FROM StockMovement sm 
        WHERE sm.item.itemId = :itemId 
        AND sm.movementType = 'IN'
        """)
    Integer calculateTotalStockIn(@Param("itemId") Long itemId);
    
    /**
     * Calculate total stock OUT for a specific item.
     */
    @Query("""
        SELECT COALESCE(SUM(sm.quantity), 0) 
        FROM StockMovement sm 
        WHERE sm.item.itemId = :itemId 
        AND sm.movementType = 'OUT'
        """)
    Integer calculateTotalStockOut(@Param("itemId") Long itemId);
    
    /**
     * Calculate current stock for a specific item (IN - OUT).
     */
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) - 
            COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0)
        FROM StockMovement sm 
        WHERE sm.item.itemId = :itemId
        """)
    Integer calculateCurrentStock(@Param("itemId") Long itemId);
    
    /**
     * Get stock summary for all items.
     * Returns a projection with itemId, totalIn, totalOut, and currentStock.
     */
    @Query("""
        SELECT 
            sm.item.itemId as itemId,
            COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) as totalIn,
            COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0) as totalOut,
            COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) - 
            COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0) as currentStock
        FROM StockMovement sm 
        GROUP BY sm.item.itemId
        """)
    List<StockSummaryProjection> getStockSummaryForAllItems();
    
    /**
     * Get stock summary for a specific item.
     */
    @Query("""
        SELECT 
            sm.item.itemId as itemId,
            COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) as totalIn,
            COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0) as totalOut,
            COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) - 
            COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0) as currentStock
        FROM StockMovement sm 
        WHERE sm.item.itemId = :itemId
        GROUP BY sm.item.itemId
        """)
    StockSummaryProjection getStockSummaryForItem(@Param("itemId") Long itemId);
    
    /**
     * Find recent stock movements across all items.
     */
    @Query("""
        SELECT sm FROM StockMovement sm 
        ORDER BY sm.createdAt DESC
        """)
    List<StockMovement> findRecentMovements();
    
    List<StockMovement> findByReferenceNumber(String referenceNumber);
    void deleteByReferenceNumber(String referenceNumber);
    
    /**
     * Find stock movements within a date range.
     */
    @Query("""
        SELECT sm FROM StockMovement sm 
        WHERE sm.createdAt BETWEEN :startDate AND :endDate 
        ORDER BY sm.createdAt DESC
        """)
    List<StockMovement> findMovementsBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Check if there are any stock movements for an item.
     * Useful for validation before deleting items.
     */
    boolean existsByItemItemId(Long itemId);
    
    /**
     * Find stock-out movements by reason type.
     * SaaS Features: Stock-out reasons tracking
     */
    @Query("""
        SELECT sm FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.reasonType = :reasonType 
        ORDER BY sm.createdAt DESC
        """)
    List<StockMovement> findStockOutsByReasonType(@Param("reasonType") String reasonType);
    
    /**
     * Count stock-out movements by reason type.
     * SaaS Features: Stock-out reasons reporting
     */
    @Query("""
        SELECT COUNT(sm) FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.reasonType = :reasonType
        """)
    Integer countByReasonType(@Param("reasonType") String reasonType);
    
    /**
     * Find stock-out movements by reason type and date range.
     * SaaS Features: Stock-out reasons filtering
     */
    @Query("""
        SELECT sm FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.reasonType = :reasonType 
        AND sm.createdAt BETWEEN :startDate AND :endDate 
        ORDER BY sm.createdAt DESC
        """)
    List<StockMovement> findStockOutsByReasonTypeAndDateRange(
        @Param("reasonType") String reasonType,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get stock-out reasons breakdown with counts and percentages.
     * SaaS Features: Stock-out reasons reporting
     */
    @Query("""
        SELECT 
            sm.reasonType as reasonType,
            COUNT(sm) as count
        FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY sm.reasonType 
        ORDER BY count DESC
        """)
    List<ReasonBreakdownProjection> getReasonBreakdown(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get stock-out reasons breakdown for a specific item.
     * SaaS Features: Stock-out reasons by item
     */
    @Query("""
        SELECT 
            sm.reasonType as reasonType,
            COUNT(sm) as count
        FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.item.itemId = :itemId 
        AND sm.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY sm.reasonType 
        ORDER BY count DESC
        """)
    List<ReasonBreakdownProjection> getReasonBreakdownByItem(
        @Param("itemId") Long itemId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get stock-out reasons breakdown for a specific category.
     * SaaS Features: Stock-out reasons by category
     */
    @Query("""
        SELECT 
            sm.reasonType as reasonType,
            COUNT(sm) as count
        FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        AND sm.item.category.categoryId = :categoryId 
        AND sm.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY sm.reasonType 
        ORDER BY count DESC
        """)
    List<ReasonBreakdownProjection> getReasonBreakdownByCategory(
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get top stock-out reasons by count.
     * SaaS Features: Top reasons analysis
     */
    @Query("""
        SELECT 
            sm.reasonType as reasonType,
            COUNT(sm) as count
        FROM StockMovement sm 
        WHERE sm.movementType = 'OUT' 
        GROUP BY sm.reasonType 
        ORDER BY count DESC
        LIMIT :limit
        """)
    List<ReasonBreakdownProjection> getTopReasons(@Param("limit") int limit);
    
    /**
     * Projection interface for stock summary queries.
     */
    interface StockSummaryProjection {
        Long getItemId();
        Integer getTotalIn();
        Integer getTotalOut();
        Integer getCurrentStock();
    }
    
    /**
     * Projection interface for reason breakdown queries.
     * SaaS Features: Stock-out reasons reporting
     */
    interface ReasonBreakdownProjection {
        String getReasonType();
        Integer getCount();
    }
}
