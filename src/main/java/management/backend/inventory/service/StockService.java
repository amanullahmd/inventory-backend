package management.backend.inventory.service;

import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StockMovementRequest;
import management.backend.inventory.dto.StockInBatchRequest;
import management.backend.inventory.dto.StockInBatchRequest.StockInLine;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.entity.StockOutReasonEnum;
import management.backend.inventory.entity.StockSourceMode;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockMovementRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.SupplierRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for stock movement operations and stock calculations.
 * Requirements: 4.1, 4.2, 4.3, 4.5 - Stock movement recording, validation, and calculations
 */
@Service
@Transactional
public class StockService {
    
    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    
    public StockService(StockMovementRepository stockMovementRepository, ItemRepository itemRepository, UserRepository userRepository, SupplierRepository supplierRepository, WarehouseRepository warehouseRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
    }
    
    /**
     * Record stock-in movement.
     * Requirements: 4.1, 2.1, 2.2 - Stock-in movement recording with positive quantity
     */
    public StockMovement recordStockIn(StockMovementRequest request, Authentication authentication) {
        // Validate that the item exists
        Long itemId = request.getItemId();
        if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found");
        }
        
        Item item = itemOpt.get();
        User currentUser = resolveCurrentUser(authentication);
        
        Long previousStock = item.getCurrentStock();
        Long newStock = previousStock + request.getQuantity();
        
        // Create and save stock-in movement
        StockMovement movement = new StockMovement(
            item,
            currentUser,
            MovementType.IN,
            request.getQuantity(),
            previousStock,
            newStock
        );
        
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setNotes(request.getNotes());
        // supplier/warehouse may be set via batch endpoint; keep null here
        
        // Update item stock
        item.setCurrentStock(newStock);
        itemRepository.save(item);
        
        return stockMovementRepository.save(movement);
    }
    
    /**
     * Record stock-in batch with a shared reference number and supplier/warehouse.
     */
    public List<StockMovement> recordStockInBatch(StockInBatchRequest request, Authentication authentication) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            Long supplierId = request.getSupplierId();
            if (supplierId == null) throw new IllegalArgumentException("Supplier ID cannot be null");
            supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            if (Boolean.FALSE.equals(supplier.getIsActive())) {
                throw new IllegalArgumentException("Supplier is inactive");
            }
        }
        Long warehouseId = request.getWarehouseId();
        if (warehouseId == null) throw new IllegalArgumentException("Warehouse ID cannot be null");
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        if (Boolean.FALSE.equals(warehouse.getIsActive())) {
            throw new IllegalArgumentException("Warehouse is inactive");
        }
        User currentUser = resolveCurrentUser(authentication);
        String ref = request.getReferenceNumber() != null && !request.getReferenceNumber().isBlank()
            ? request.getReferenceNumber()
            : generateReferenceNumber();
        
        final Supplier supplierFinal = supplier;
        return request.getItems().stream().map(line -> {
            Long itemId = line.getItemId();
            if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
            Long previousStock = item.getCurrentStock();
            Long newStock = previousStock + line.getQuantity();
            StockMovement movement = new StockMovement(
                item, currentUser, MovementType.IN, line.getQuantity(), previousStock, newStock
            );
            movement.setReferenceNumber(ref);
            movement.setNotes(request.getNotes());
            if (supplierFinal != null) movement.setSupplier(supplierFinal);
            movement.setWarehouse(warehouse);
            movement.setSourceMode(supplierFinal != null ? StockSourceMode.SUPPLIER : StockSourceMode.NON_SUPPLIER);
            item.setCurrentStock(newStock);
            itemRepository.save(item);
            return stockMovementRepository.save(movement);
        }).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getStockInDetails(String referenceNumber) {
        List<StockMovement> movements = stockMovementRepository.findByReferenceNumber(referenceNumber);
        return movements.stream()
            .map(m -> {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("itemId", m.getItem().getItemId());
                row.put("sku", m.getItem().getSku());
                row.put("name", m.getItem().getName());
                row.put("quantity", m.getQuantity());
                row.put("createdAt", m.getCreatedAt());
                row.put("supplierId", m.getSupplier() != null ? m.getSupplier().getSupplierId() : null);
                row.put("warehouseId", m.getWarehouse() != null ? m.getWarehouse().getWarehouseId() : null);
                return row;
            })
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteStockIn(String referenceNumber) {
        List<StockMovement> movements = stockMovementRepository.findByReferenceNumber(referenceNumber);
        if (movements.isEmpty()) return;
        for (StockMovement m : movements) {
            Item item = m.getItem();
            item.setCurrentStock(item.getCurrentStock() - m.getQuantity());
            itemRepository.save(item);
        }
        stockMovementRepository.deleteByReferenceNumber(referenceNumber);
    }
    
    @Transactional
    public void updateStockIn(String referenceNumber, StockInBatchRequest request, Authentication authentication) {
        List<StockMovement> existing = stockMovementRepository.findByReferenceNumber(referenceNumber);
        java.time.LocalDateTime originalCreated = existing.isEmpty() ? null : existing.stream().map(StockMovement::getCreatedAt).min(java.util.Comparator.naturalOrder()).orElse(null);
        deleteStockIn(referenceNumber);
        // Recreate with the same reference number
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            Long supplierId = request.getSupplierId();
            if (supplierId == null) throw new IllegalArgumentException("Supplier ID cannot be null");
            supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            if (Boolean.FALSE.equals(supplier.getIsActive())) {
                throw new IllegalArgumentException("Supplier is inactive");
            }
        }
        Long warehouseId = request.getWarehouseId();
        if (warehouseId == null) throw new IllegalArgumentException("Warehouse ID cannot be null");
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        if (Boolean.FALSE.equals(warehouse.getIsActive())) {
            throw new IllegalArgumentException("Warehouse is inactive");
        }
        User currentUser = resolveCurrentUser(authentication);
        List<StockInLine> lines = request.getItems();
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("No items provided");
        String newRef = (request.getReferenceNumber() != null && !request.getReferenceNumber().isBlank()) ? request.getReferenceNumber() : referenceNumber;
        for (StockInLine line : lines) {
            Long itemId = line.getItemId();
            if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
            Long previousStock = item.getCurrentStock();
            Long newStock = previousStock + line.getQuantity();
            StockMovement movement = new StockMovement(item, currentUser, MovementType.IN, line.getQuantity(), previousStock, newStock);
            movement.setReferenceNumber(newRef);
            movement.setNotes(request.getNotes());
            if (supplier != null) movement.setSupplier(supplier);
            movement.setWarehouse(warehouse);
            if (originalCreated != null) movement.setCreatedAt(originalCreated);
            movement.setSourceMode(supplier != null ? StockSourceMode.SUPPLIER : StockSourceMode.NON_SUPPLIER);
            item.setCurrentStock(newStock);
            itemRepository.save(item);
            stockMovementRepository.save(movement);
        }
    }
    
    private String generateReferenceNumber() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return String.format("SI-%s", now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
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
    
    /**
     * Record stock-out movement with validation.
     * Requirements: 4.2, 4.3, 2.1, 2.2 - Stock-out validation and recording with sufficient stock check
     * SaaS Features: Stock-out reason tracking
     */
    public StockMovement recordStockOut(StockMovementRequest request, Authentication authentication) {
        // Validate that the item exists
        Long itemId = request.getItemId();
        if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found");
        }
        
        Item item = itemOpt.get();
        User currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Long previousStock = item.getCurrentStock();
        
        // Validate sufficient stock exists
        if (previousStock < request.getQuantity()) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock for item '%s'. Current stock: %d, Requested: %d", 
                    item.getName(), previousStock, request.getQuantity())
            );
        }
        
        // Validate reason is provided for stock-out
        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException("Reason is required for stock-out operations");
        }
        
        // Validate reason type if provided
        StockOutReasonEnum reasonType = null;
        if (request.getReasonType() != null && !request.getReasonType().isBlank()) {
            try {
                reasonType = StockOutReasonEnum.valueOf(request.getReasonType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid reason type: " + request.getReasonType());
            }
        }
        
        Long newStock = previousStock - request.getQuantity();
        
        // Create and save stock-out movement
        StockMovement movement = new StockMovement(
            item,
            currentUser,
            MovementType.OUT,
            request.getQuantity(),
            previousStock,
            newStock
        );
        
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setNotes(request.getNotes());
        movement.setReason(request.getReason());
        movement.setRecipient(request.getRecipient());
        movement.setReasonType(reasonType);
        movement.setSourceMode(StockSourceMode.NON_SUPPLIER);
        
        // Update item stock
        item.setCurrentStock(newStock);
        itemRepository.save(item);
        
        return stockMovementRepository.save(movement);
    }
    
    /**
     * Get stock summary for all items.
     * Requirements: 4.5 - Stock summary calculation methods
     */
    @Transactional(readOnly = true)
    public List<ItemStockResponse> getStockSummaryForAllItems() {
        List<Item> items = itemRepository.findAllItemsOrderByCreatedAt();
        
        // Get stock summaries for all items
        List<StockMovementRepository.StockSummaryProjection> stockSummaries = 
            stockMovementRepository.getStockSummaryForAllItems();
        
        // Create a map for quick lookup of stock data by item ID
        Map<Long, StockMovementRepository.StockSummaryProjection> stockMap = stockSummaries.stream()
            .collect(Collectors.toMap(
                StockMovementRepository.StockSummaryProjection::getItemId,
                summary -> summary
            ));
        
        // Convert items to response DTOs with stock information
        return items.stream()
            .map(item -> {
                StockMovementRepository.StockSummaryProjection stockSummary = stockMap.get(item.getItemId());
                
                Integer totalStockIn = stockSummary != null ? stockSummary.getTotalIn() : 0;
                Integer totalStockOut = stockSummary != null ? stockSummary.getTotalOut() : 0;
                Integer currentStock = stockSummary != null ? stockSummary.getCurrentStock() : 0;
                Long categoryId = item.getCategory() != null ? item.getCategory().getCategoryId() : null;
                String categoryName = item.getCategory() != null ? item.getCategory().getName() : null;
                return new ItemStockResponse(
                    item.getItemId(),
                    item.getName(),
                    item.getSku(),
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getCreatedAt(),
                    currentStock,
                    totalStockIn,
                    totalStockOut,
                    categoryId,
                    categoryName,
                    item.getMinimumStock(),
                    item.getMaximumStock(),
                    item.getReorderLevel()
                );
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get stock summary for a specific item.
     * Requirements: 4.5 - Stock summary calculation for individual items
     */
    @Transactional(readOnly = true)
    public Optional<ItemStockResponse> getStockSummaryForItem(Long itemId) {
        if (itemId == null) {
            return Optional.empty();
        }
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Item item = itemOpt.get();
        StockMovementRepository.StockSummaryProjection stockSummary = 
            stockMovementRepository.getStockSummaryForItem(itemId);
        
        Integer totalStockIn = stockSummary != null ? stockSummary.getTotalIn() : 0;
        Integer totalStockOut = stockSummary != null ? stockSummary.getTotalOut() : 0;
        Integer currentStock = stockSummary != null ? stockSummary.getCurrentStock() : 0;
        
        Long categoryId = item.getCategory() != null ? item.getCategory().getCategoryId() : null;
        String categoryName = item.getCategory() != null ? item.getCategory().getName() : null;
        ItemStockResponse response = new ItemStockResponse(
            item.getItemId(),
            item.getName(),
            item.getSku(),
            item.getDescription(),
            item.getUnitPrice(),
            item.getCreatedAt(),
            currentStock,
            totalStockIn,
            totalStockOut,
            categoryId,
            categoryName,
            item.getMinimumStock(),
            item.getMaximumStock(),
            item.getReorderLevel()
        );
        
        return Optional.of(response);
    }
    
    /**
     * Get current stock level for a specific item.
     * Requirements: 4.5 - Current stock calculation
     */
    @Transactional(readOnly = true)
    public Integer getCurrentStock(Long itemId) {
        Integer currentStock = stockMovementRepository.calculateCurrentStock(itemId);
        return currentStock != null ? currentStock : 0;
    }
    
    /**
     * Get total stock-in for a specific item.
     * Requirements: 4.5 - Stock calculation methods
     */
    @Transactional(readOnly = true)
    public Integer getTotalStockIn(Long itemId) {
        Integer totalIn = stockMovementRepository.calculateTotalStockIn(itemId);
        return totalIn != null ? totalIn : 0;
    }
    
    /**
     * Get total stock-out for a specific item.
     * Requirements: 4.5 - Stock calculation methods
     */
    @Transactional(readOnly = true)
    public Integer getTotalStockOut(Long itemId) {
        Integer totalOut = stockMovementRepository.calculateTotalStockOut(itemId);
        return totalOut != null ? totalOut : 0;
    }
    
    /**
     * Get all stock movements for a specific item.
     * Requirements: 4.5 - Stock movement history retrieval
     */
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsForItem(Long itemId) {
        return stockMovementRepository.findByItemItemIdOrderByCreatedAtDesc(itemId);
    }
    
    /**
     * Get recent stock movements across all items.
     * Requirements: 4.5, 2.2, 2.4 - Recent movement tracking
     */
    @Transactional(readOnly = true)
    public List<StockMovement> getRecentStockMovements() {
        return stockMovementRepository.findRecentMovements();
    }
    
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getStockInSummaries() {
        List<StockMovement> ins = getStockInTransactions();
        java.util.Map<String, java.util.List<StockMovement>> grouped = ins.stream()
            .filter(m -> m.getReferenceNumber() != null && !m.getReferenceNumber().isBlank())
            .collect(java.util.stream.Collectors.groupingBy(StockMovement::getReferenceNumber));
        java.util.List<java.util.Map<String, Object>> summaries = new java.util.ArrayList<>();
        for (var entry : grouped.entrySet()) {
            String ref = entry.getKey();
            java.util.List<StockMovement> rows = entry.getValue();
            StockMovement first = rows.get(0);
            String createdBy = first.getUser() != null ? first.getUser().getName() : null;
            java.time.LocalDateTime createdAt = rows.stream().map(StockMovement::getCreatedAt).min(java.util.Comparator.naturalOrder()).orElse(first.getCreatedAt());
            java.time.LocalDateTime updatedAt = rows.stream().map(StockMovement::getCreatedAt).max(java.util.Comparator.naturalOrder()).orElse(first.getCreatedAt());
            String supplierName = first.getSupplier() != null ? first.getSupplier().getName() : null;
            String sourceMode = first.getSourceMode() != null ? first.getSourceMode().name() : null;
            java.util.Map<String, Object> row = new java.util.HashMap<>();
            row.put("referenceNumber", ref);
            row.put("count", rows.size());
            row.put("createdBy", createdBy);
            row.put("createdAt", createdAt);
            row.put("updatedAt", updatedAt);
            row.put("supplierName", supplierName);
            row.put("sourceMode", sourceMode);
            summaries.add(row);
        }
        summaries.sort((a,b) -> ((java.time.LocalDateTime)b.get("updatedAt")).compareTo((java.time.LocalDateTime)a.get("updatedAt")));
        return summaries;
    }

    /**
     * Get stock in transactions.
     * Requirements: 2.2, 2.4 - Stock in transactions
     */
    @Transactional(readOnly = true)
    public List<StockMovement> getStockInTransactions() {
        return getRecentStockMovements().stream()
            .filter(m -> m.getMovementType() == MovementType.IN)
            .collect(Collectors.toList());
    }

    /**
     * Get stock out transactions.
     * Requirements: 2.2, 2.4 - Stock out transactions
     */
    @Transactional(readOnly = true)
    public List<StockMovement> getStockOutTransactions() {
        return getRecentStockMovements().stream()
            .filter(m -> m.getMovementType() == MovementType.OUT)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if sufficient stock exists for a stock-out operation.
     * Requirements: 4.2, 4.3 - Stock availability validation
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientStock(Long itemId, Integer requestedQuantity) {
        Integer currentStock = getCurrentStock(itemId);
        return currentStock >= requestedQuantity;
    }
    
    /**
     * Validate stock movement request.
     * Requirements: 4.1, 4.2 - Stock movement validation
     */
    public void validateStockMovementRequest(StockMovementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Stock movement request cannot be null");
        }
        
        if (request.getItemId() == null) {
            throw new IllegalArgumentException("Item ID is required");
        }
        
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Validate that the item exists
        Long itemId = request.getItemId();
        if (itemId == null || !itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found");
        }
    }
    
    /**
     * Get list of predefined stock-out reasons.
     * SaaS Features: Stock-out reasons tracking
     */
    @Transactional(readOnly = true)
    public List<String> getStockOutReasons() {
        return Arrays.stream(StockOutReasonEnum.values())
            .map(StockOutReasonEnum::name)
            .collect(Collectors.toList());
    }
    
    /**
     * Validate stock-out reason.
     * SaaS Features: Stock-out reasons validation
     */
    public boolean validateStockOutReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return false;
        }
        
        // Check if it's a predefined reason
        if (StockOutReasonEnum.isValid(reason)) {
            return true;
        }
        
        // Custom reasons are allowed (max 100 chars)
        return reason.length() <= 100;
    }
}
