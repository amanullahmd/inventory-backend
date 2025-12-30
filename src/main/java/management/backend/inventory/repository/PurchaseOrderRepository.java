package management.backend.inventory.repository;

import management.backend.inventory.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplier_SupplierId(Long supplierId);
    List<PurchaseOrder> findByWarehouse_WarehouseId(Long warehouseId);
}
