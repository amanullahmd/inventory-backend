package management.backend.inventory.repository;

import management.backend.inventory.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
    List<SalesOrderItem> findBySalesOrderSalesOrderId(Long salesOrderId);
    List<SalesOrderItem> findByItemItemId(Long itemId);
}
