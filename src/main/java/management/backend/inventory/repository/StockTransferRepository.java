package management.backend.inventory.repository;

import management.backend.inventory.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    List<StockTransfer> findByItemItemId(Long itemId);
    List<StockTransfer> findByFromWarehouseWarehouseId(Long warehouseId);
    List<StockTransfer> findByToWarehouseWarehouseId(Long warehouseId);
}
