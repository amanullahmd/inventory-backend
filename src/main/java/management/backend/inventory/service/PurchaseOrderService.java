package management.backend.inventory.service;

import management.backend.inventory.dto.CreatePurchaseOrderRequest;
import management.backend.inventory.entity.PurchaseOrder;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.PurchaseOrderRepository;
import management.backend.inventory.repository.SupplierRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                SupplierRepository supplierRepository,
                                WarehouseRepository warehouseRepository,
                                UserRepository userRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(CreatePurchaseOrderRequest request, Authentication authentication) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        User currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setWarehouse(warehouse);
        po.setOrderDate(LocalDate.parse(request.getOrderDate()));
        if (request.getExpectedDeliveryDate() != null && !request.getExpectedDeliveryDate().isBlank()) {
            po.setExpectedDeliveryDate(LocalDate.parse(request.getExpectedDeliveryDate()));
        }
        po.setNotes(request.getNotes());
        po.setCreatedBy(currentUser);
        po.setStatus("DRAFT");
        return purchaseOrderRepository.save(po);
    }
}
