package management.backend.inventory.service;

import management.backend.inventory.dto.CreateSalesOrderRequest;
import management.backend.inventory.entity.SalesOrder;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.SalesOrderRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    public SalesOrderService(SalesOrderRepository salesOrderRepository,
                             WarehouseRepository warehouseRepository,
                             UserRepository userRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    @Transactional
    public SalesOrder createSalesOrder(CreateSalesOrderRequest request, Authentication authentication) {
        Long warehouseId = request.getWarehouseId();
        if (warehouseId == null) throw new IllegalArgumentException("Warehouse ID is required");
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        User currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SalesOrder so = new SalesOrder();
        so.setWarehouse(warehouse);
        so.setOrderDate(LocalDate.parse(request.getOrderDate()));
        if (request.getDeliveryDate() != null && !request.getDeliveryDate().isBlank()) {
            so.setDeliveryDate(LocalDate.parse(request.getDeliveryDate()));
        }
        so.setCustomerName(request.getCustomerName());
        so.setCustomerEmail(request.getCustomerEmail());
        so.setNotes(request.getNotes());
        so.setCreatedBy(currentUser);
        so.setStatus("DRAFT");
        return salesOrderRepository.save(so);
    }
}
