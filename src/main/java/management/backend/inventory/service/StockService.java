package management.backend.inventory.service;

import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StockMovementRequest;
import management.backend.inventory.dto.StockInBatchRequest;
import management.backend.inventory.dto.StockInBatchRequest.StockInLine;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.entity.StockOutReasonEnum;
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
        Optional<Item> itemOpt = itemRepository.findById(request.getItemId());
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item with ID " + request.getItemId() + " not found");
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
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        if (Boolean.FALSE.equals(supplier.getIsActive()) || Boolean.FALSE.equals(warehouse.getIsActive())) {
            throw new IllegalArgumentException("Supplier or warehouse is inactive");
        }
        User currentUser = resolveCurrentUser(authentication);
        String ref = request.getReferenceNumber() != null && !request.getReferenceNumber().isBlank()
            ? request.getReferenceNumber()
            : generateReferenceNumber();
        
        return request.getItems().stream().map(line -> {
            Item item = itemRepository.findById(line.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
            Long previousStock = item.getCurrentStock();
            Long newStock = previousStock + line.getQuantity();
            StockMovement movement = new StockMovement(
                item, currentUser, MovementType.IN, line.getQuantity(), previousStock, newStock
            );
            movement.setReferenceNumber(ref);
            movement.setNotes(request.getNotes());
            movement.setSupplier(supplier);
            movement.setWarehouse(warehouse);
            item.setCurrentStock(newStock);
            itemRepository.save(item);
            return stockMovementRepository.save(movement);
        }).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getStockInDetails(String referenceNumber) {
        List<StockMovement> movements = stockMovementRepository.findByReferenceNumber(referenceNumber);
        return movements.stream()
            .map(m -> java.util.Map.<String, Object>of(
                "itemId", m.getItem().getItemId(),
                "sku", m.getItem().getSku(),
                "name", m.getItem().getName(),
                "quantity", m.getQuantity(),
                "createdAt", m.getCreatedAt(),
                "supplierId", m.getSupplier() != null ? m.getSupplier().getSupplierId() : null,
                "warehouseId", m.getWarehouse() != null ? m.getWarehouse().getWarehouseId() : null
            ))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteStockIn(String referenceNumber) {
        List<StockMovement> movements = stockMovementRepository.findByReferenceNumber(referenceNumber);
        if (movements.isEmpty()) return;
        java.time.LocalDate today = java.time.LocalDate.now();
        boolean sameDay = movements.stream().allMatch(m -> m.getCreatedAt().toLocalDate().equals(today));
        if (!sameDay) {
            throw new IllegalArgumentException("Cannot modify past stock-in batches");
        }
        for (StockMovement m : movements) {
            Item item = m.getItem();
            item.setCurrentStock(item.getCurrentStock() - m.getQuantity());
            itemRepository.save(item);
        }
        stockMovementRepository.deleteByReferenceNumber(referenceNumber);
    }
    
    @Transactional
    public void updateStockIn(String referenceNumber, StockInBatchRequest request, Authentication authentication) {
        deleteStockIn(referenceNumber);
        // Recreate with the same reference number
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        if (Boolean.FALSE.equals(supplier.getIsActive()) || Boolean.FALSE.equals(warehouse.getIsActive())) {
            throw new IllegalArgumentException("Supplier or warehouse is inactive");
        }
        User currentUser = resolveCurrentUser(authentication);
        List<StockInLine> lines = request.getItems();
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("No items provided");
        for (StockInLine line : lines) {
            Item item = itemRepository.findById(line.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
            Long previousStock = item.getCurrentStock();
            Long newStock = previousStock + line.getQuantity();
            StockMovement movement = new StockMovement(item, currentUser, MovementType.IN, line.getQuantity(), previousStock, newStock);
            movement.setReferenceNumber(referenceNumber);
            movement.setNotes(request.getNotes());
            movement.setSupplier(supplier);
            movement.setWarehouse(warehouse);
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
        Optional<Item> itemOpt = itemRepository.findById(request.getItemId());
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item with ID " + request.getItemId() + " not found");
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
                    item.getUnitPrice(),
                    item.getCreatedAt(),
                    currentStock,
                    totalStockIn,
                    totalStockOut,
                    categoryId,
                    categoryName
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
            item.getUnitPrice(),
            item.getCreatedAt(),
            currentStock,
            totalStockIn,
            totalStockOut,
            categoryId,
            categoryName
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
            java.time.LocalDateTime createdAt = first.getCreatedAt();
            summaries.add(java.util.Map.of(
                "referenceNumber", ref,
                "count", rows.size(),
                "createdBy", createdBy,
                "createdAt", createdAt
            ));
        }
        summaries.sort((a,b) -> ((java.time.LocalDateTime)b.get("createdAt")).compareTo((java.time.LocalDateTime)a.get("createdAt")));
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
        if (!itemRepository.existsById(request.getItemId())) {
            throw new IllegalArgumentException("Item with ID " + request.getItemId() + " not found");
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
