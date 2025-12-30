package management.backend.inventory.repository;

import management.backend.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByItemItemId(Long itemId);
    List<InventoryTransaction> findByWarehouseWarehouseId(Long warehouseId);
    List<InventoryTransaction> findByTransactionType(String transactionType);
    
    @Query("""
           SELECT COALESCE(SUM(CASE WHEN t.transactionType IN ('STOCK_IN', 'TRANSFER_IN') THEN t.quantity ELSE -t.quantity END), 0)
           FROM InventoryTransaction t 
           WHERE t.item.itemId = :itemId AND t.warehouse.warehouseId = :warehouseId
           """)
    Long getAvailableStock(Long itemId, Long warehouseId);
}
