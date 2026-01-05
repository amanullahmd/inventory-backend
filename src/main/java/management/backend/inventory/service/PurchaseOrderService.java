package management.backend.inventory.service;

import management.backend.inventory.dto.CreatePurchaseOrderRequest;
import management.backend.inventory.dto.AddPurchaseOrderItemsRequest;
import management.backend.inventory.entity.PurchaseOrder;
import management.backend.inventory.entity.PurchaseOrderItem;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.entity.PurchaseOrderStatus;
import management.backend.inventory.dto.PurchaseOrderResponse;
import management.backend.inventory.repository.PurchaseOrderRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.SupplierRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.WarehouseRepository;
import management.backend.inventory.repository.PurchaseOrderItemRepository;
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
    private final ItemRepository itemRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                SupplierRepository supplierRepository,
                                WarehouseRepository warehouseRepository,
                                UserRepository userRepository,
                                ItemRepository itemRepository,
                                PurchaseOrderItemRepository purchaseOrderItemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(CreatePurchaseOrderRequest request, Authentication authentication) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        if (supplier.getIsActive() == null || !supplier.getIsActive()) {
            throw new IllegalArgumentException("Supplier is inactive and cannot be used for purchase orders");
        }
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        User currentUser = resolveCurrentUser(authentication);

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setWarehouse(warehouse);
        po.setOrderDate(LocalDate.parse(request.getOrderDate()));
        if (request.getExpectedDeliveryDate() != null && !request.getExpectedDeliveryDate().isBlank()) {
            po.setExpectedDeliveryDate(LocalDate.parse(request.getExpectedDeliveryDate()));
        }
        po.setNotes(request.getNotes());
        po.setCreatedBy(currentUser);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po = purchaseOrderRepository.save(po);
        String code = String.format("PO-%s-%d", po.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")), po.getPurchaseOrderId());
        po.setPurchaseOrderCode(code);
        return purchaseOrderRepository.save(po);
    }
    
    @Transactional
    public PurchaseOrder addItems(Long purchaseOrderId, AddPurchaseOrderItemsRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item line is required");
        }
        java.math.BigDecimal total = po.getTotalAmount() != null ? po.getTotalAmount() : java.math.BigDecimal.ZERO;
        for (AddPurchaseOrderItemsRequest.Line line : request.getItems()) {
            Item item = itemRepository.findById(line.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
            PurchaseOrderItem poi = new PurchaseOrderItem(po, item, line.getQuantity(), line.getUnitPrice());
            po.getItems().add(poi);
            total = total.add(line.getUnitPrice().multiply(new java.math.BigDecimal(line.getQuantity())));
        }
        po.setTotalAmount(total);
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            String notes = po.getNotes();
            po.setNotes(notes == null ? request.getNotes() : notes + "\n" + request.getNotes());
        }
        return purchaseOrderRepository.save(po);
    }
    
    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, management.backend.inventory.dto.UpdatePurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        if (request.getExpectedDeliveryDate() != null && !request.getExpectedDeliveryDate().isBlank()) {
            po.setExpectedDeliveryDate(LocalDate.parse(request.getExpectedDeliveryDate()));
        }
        if (request.getNotes() != null) {
            po.setNotes(request.getNotes());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                PurchaseOrderStatus st = PurchaseOrderStatus.valueOf(request.getStatus());
                po.setStatus(st);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.getStatus());
            }
        }
        return purchaseOrderRepository.save(po);
    }
    
    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT orders can be deleted");
        }
        purchaseOrderRepository.delete(po);
    }
    
    @Transactional(readOnly = true)
    public management.backend.inventory.dto.PurchaseOrderDetailResponse getPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        java.util.List<management.backend.inventory.dto.PurchaseOrderItemResponse> itemDtos =
            purchaseOrderItemRepository.findByPurchaseOrder_PurchaseOrderId(id).stream()
                .map(poi -> new management.backend.inventory.dto.PurchaseOrderItemResponse(
                    poi.getPurchaseOrderItemId(),
                    poi.getItem().getItemId(),
                    poi.getItem().getSku(),
                    poi.getItem().getName(),
                    poi.getQuantity(),
                    poi.getUnitPrice(),
                    poi.getLineTotal()
                ))
                .toList();
        management.backend.inventory.dto.PurchaseOrderResponse base = toResponse(po);
        return new management.backend.inventory.dto.PurchaseOrderDetailResponse(
            base.getPurchaseOrderId(), base.getPurchaseOrderCode(), base.getSupplierId(), base.getSupplierName(),
            base.getWarehouseId(), base.getWarehouseName(), base.getStatus(), base.getOrderDate(),
            base.getExpectedDeliveryDate(), base.getTotalAmount(), base.getNotes(), base.getCreatedAt(), itemDtos
        );
    }
    
    @Transactional
    public PurchaseOrder replaceItems(Long id, AddPurchaseOrderItemsRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        // Clear current items
        List<PurchaseOrderItem> existing = purchaseOrderItemRepository.findByPurchaseOrder_PurchaseOrderId(id);
        for (PurchaseOrderItem e : existing) {
            purchaseOrderItemRepository.delete(e);
        }
        po.getItems().clear();
        // Add new items
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (AddPurchaseOrderItemsRequest.Line line : request.getItems()) {
            Item item = itemRepository.findById(line.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
            PurchaseOrderItem poi = new PurchaseOrderItem(po, item, line.getQuantity(), line.getUnitPrice());
            po.getItems().add(poi);
            total = total.add(line.getUnitPrice().multiply(new java.math.BigDecimal(line.getQuantity())));
        }
        po.setTotalAmount(total);
        return purchaseOrderRepository.save(po);
    }
    
    @Transactional
    public PurchaseOrder deleteItem(Long purchaseOrderItemId) {
        PurchaseOrderItem poi = purchaseOrderItemRepository.findById(purchaseOrderItemId)
            .orElseThrow(() -> new IllegalArgumentException("Purchase order item not found"));
        PurchaseOrder po = poi.getPurchaseOrder();
        purchaseOrderItemRepository.delete(poi);
        // Recalculate total
        java.math.BigDecimal total = purchaseOrderItemRepository.findByPurchaseOrder_PurchaseOrderId(po.getPurchaseOrderId()).stream()
            .map(PurchaseOrderItem::getLineTotal)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        po.setTotalAmount(total);
        return purchaseOrderRepository.save(po);
    }
    
    private User resolveCurrentUser(Authentication authentication) {
        String name = authentication.getName();
        try {
            Long userId = Long.parseLong(name);
            return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
    }
    
    public PurchaseOrderResponse toResponse(PurchaseOrder po) {
        Long supplierId = po.getSupplier() != null ? po.getSupplier().getSupplierId() : null;
        String supplierName = po.getSupplier() != null ? po.getSupplier().getName() : null;
        Long warehouseId = po.getWarehouse() != null ? po.getWarehouse().getWarehouseId() : null;
        String warehouseName = po.getWarehouse() != null ? po.getWarehouse().getName() : null;
        return new PurchaseOrderResponse(
            po.getPurchaseOrderId(),
            po.getPurchaseOrderCode(),
            supplierId,
            supplierName,
            warehouseId,
            warehouseName,
            po.getStatus() != null ? po.getStatus().name() : null,
            po.getOrderDate(),
            po.getExpectedDeliveryDate(),
            po.getTotalAmount(),
            po.getNotes(),
            po.getCreatedAt()
        );
    }
}
