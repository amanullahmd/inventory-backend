package management.backend.inventory.repository;

import management.backend.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entity operations.
 * Requirements: 3.2, 3.3 - Item repository with custom query methods and stock calculations
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find item by SKU (Stock Keeping Unit).
     * SKU is unique, so this should return at most one item.
     */
    Optional<Item> findBySku(String sku);
    
    /**
     * Check if an item exists with the given SKU.
     * Useful for validation before creating new items.
     */
    boolean existsBySku(String sku);
    
    /**
     * Find items by name containing the given text (case-insensitive).
     * Useful for search functionality.
     */
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Item> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all items with their stock calculations.
     * This query joins with stock_movements to calculate current stock for each item.
     * Returns items with calculated stock information.
     */
    @Query("""
        SELECT i FROM Item i 
        ORDER BY i.createdAt DESC
        """)
    List<Item> findAllItemsOrderByCreatedAt();
    
    /**
     * Find items that have stock movements (items that have been used in inventory).
     * Useful for filtering out items that have never been stocked.
     */
    @Query("""
        SELECT DISTINCT i FROM Item i 
        JOIN StockMovement sm ON sm.item = i 
        ORDER BY i.name
        """)
    List<Item> findItemsWithStockMovements();
    
    /**
     * Find items with low or zero stock.
     * This is a complex query that calculates current stock and filters items with stock below threshold.
     */
    @Query("""
        SELECT i FROM Item i 
        WHERE i.itemId IN (
            SELECT sm.item.itemId FROM StockMovement sm 
            GROUP BY sm.item.itemId 
            HAVING (
                COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE 0 END), 0) - 
                COALESCE(SUM(CASE WHEN sm.movementType = 'OUT' THEN sm.quantity ELSE 0 END), 0)
            ) <= :threshold
        )
        OR i.itemId NOT IN (SELECT DISTINCT sm2.item.itemId FROM StockMovement sm2)
        ORDER BY i.name
        """)
    List<Item> findItemsWithStockBelowThreshold(int threshold);
}