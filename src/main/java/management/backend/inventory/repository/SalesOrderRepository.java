package management.backend.inventory.repository;

import management.backend.inventory.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findByWarehouse_WarehouseId(Long warehouseId);
}
