package management.backend.inventory.service;

import management.backend.inventory.dto.CreateItemRequest;
import management.backend.inventory.dto.ItemStockResponse;
import management.backend.inventory.dto.StatisticsResponse;
import management.backend.inventory.entity.Category;
import management.backend.inventory.entity.Item;
import management.backend.inventory.repository.CategoryRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for item management business logic.
 * Requirements: 3.1, 3.2, 3.4 - Item creation, validation, and retrieval with stock calculations
 */
@Service
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final CategoryRepository categoryRepository;
    
    public ItemService(ItemRepository itemRepository, StockMovementRepository stockMovementRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Create a new item with validation.
     * Requirements: 3.1 - Item creation with validation
     */
    public Item createItem(CreateItemRequest request) {
        // Validate that SKU is unique
        if (itemRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Item with SKU '" + request.getSku() + "' already exists");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));

        // Create and save the item
        Item item = new Item();
        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setUnitPrice(request.getUnitPrice());
        item.setCategory(category);
        item.setCurrentStock(0L);
        item.setMinimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0L);
        item.setIsActive(true);

        // Set optional description
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            item.setDescription(request.getDescription());
        }
        
        if (request.getMaximumStock() != null) {
            item.setMaximumStock(request.getMaximumStock());
        }
        
        if (request.getReorderLevel() != null) {
            item.setReorderLevel(request.getReorderLevel());
        }
        
        // Category is mandatory above
        
        return itemRepository.save(item);
    }

    /**
     * Update an existing item.
     * Allows correcting wrong information such as name, SKU, unit price, description and category.
     */
    public Item updateItem(Long itemId, CreateItemRequest request) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Validate SKU uniqueness if changed
        if (request.getSku() != null && !request.getSku().isBlank()) {
            Optional<Item> existingBySku = itemRepository.findBySku(request.getSku());
            if (existingBySku.isPresent() && !existingBySku.get().getItemId().equals(itemId)) {
                throw new IllegalArgumentException("Item with SKU '" + request.getSku() + "' already exists");
            }
            item.setSku(request.getSku());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }

        if (request.getUnitPrice() != null) {
            item.setUnitPrice(request.getUnitPrice());
        }

        item.setDescription(request.getDescription());

        if (request.getMinimumStock() != null) {
            item.setMinimumStock(request.getMinimumStock());
        }
        if (request.getMaximumStock() != null) {
            item.setMaximumStock(request.getMaximumStock());
        }
        if (request.getReorderLevel() != null) {
            item.setReorderLevel(request.getReorderLevel());
        }

        if (request.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());
            item.setCategory(category.orElse(null));
        }

        return itemRepository.save(item);
    }
    
    /**
     * Retrieve all items with current stock calculations.
     * Requirements: 3.2, 3.4 - Item retrieval with stock calculations
     */
    @Transactional(readOnly = true)
    public List<ItemStockResponse> getAllItemsWithStock() {
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
     * Find item by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Item> findById(Long itemId) {
        return itemRepository.findById(itemId);
    }
    
    /**
     * Find item by SKU.
     */
    @Transactional(readOnly = true)
    public Optional<Item> findBySku(String sku) {
        return itemRepository.findBySku(sku);
    }
    
    /**
     * Get item with stock information by ID.
     */
    @Transactional(readOnly = true)
    public Optional<ItemStockResponse> getItemWithStock(Long itemId) {
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
     * Search items by name (case-insensitive).
     */
    @Transactional(readOnly = true)
    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get items with low stock (below threshold).
     */
    @Transactional(readOnly = true)
    public List<ItemStockResponse> getItemsWithLowStock(int threshold) {
        List<Item> lowStockItems = itemRepository.findItemsWithStockBelowThreshold(threshold);
        
        return lowStockItems.stream()
            .map(item -> {
                StockMovementRepository.StockSummaryProjection stockSummary = 
                    stockMovementRepository.getStockSummaryForItem(item.getItemId());
                
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
     * Check if an item exists by ID.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long itemId) {
        return itemRepository.existsById(itemId);
    }
    
    /**
     * Check if an item exists by SKU.
     */
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return itemRepository.existsBySku(sku);
    }

    /**
     * Get dashboard statistics.
     * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5 - Calculate dashboard statistics
     */
    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics() {
        List<ItemStockResponse> allItems = getAllItemsWithStock();
        
        // Calculate total items count
        Integer totalItems = allItems.size();
        
        // Calculate total inventory value (sum of unit_price × current_stock)
        BigDecimal totalValue = allItems.stream()
            .map(item -> {
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                return price.multiply(new BigDecimal(item.getCurrentStock()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate low stock items count (1-9 units)
        Integer lowStockItems = (int) allItems.stream()
            .filter(item -> item.getCurrentStock() >= 1 && item.getCurrentStock() <= 9)
            .count();
        
        // Calculate out of stock items count (0 units)
        Integer outOfStockItems = (int) allItems.stream()
            .filter(item -> item.getCurrentStock() == 0)
            .count();
        
        return new StatisticsResponse(totalItems, totalValue, lowStockItems, outOfStockItems);
    }
}
