package management.backend.inventory.service;

import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StockMovementRequest;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.MovementType;
import management.backend.inventory.entity.StockMovement;
import management.backend.inventory.entity.StockOutReasonEnum;
import management.backend.inventory.entity.User;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockMovementRepository;
import management.backend.inventory.repository.UserRepository;
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
    
    public StockService(StockMovementRepository stockMovementRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
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
        User currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
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
        
        // Update item stock
        item.setCurrentStock(newStock);
        itemRepository.save(item);
        
        return stockMovementRepository.save(movement);
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
