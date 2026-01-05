package management.backend.inventory.service;

import management.backend.inventory.dto.CreateStockOutBatchRequest;
import management.backend.inventory.dto.CreateStockOutRequest;
import management.backend.inventory.dto.StockOutItemRequest;
import management.backend.inventory.dto.StockOutResponse;
import management.backend.inventory.entity.*;
import management.backend.inventory.repository.EmployeeRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockMovementRepository;
import management.backend.inventory.repository.StockOutRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockOutService {

    private final StockOutRepository stockOutRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final EmployeeRepository employeeRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    public StockOutService(StockOutRepository stockOutRepository, ItemRepository itemRepository,
                           WarehouseRepository warehouseRepository, EmployeeRepository employeeRepository,
                           StockMovementRepository stockMovementRepository, UserRepository userRepository) {
        this.stockOutRepository = stockOutRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.employeeRepository = employeeRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.userRepository = userRepository;
    }

    private User resolveCurrentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Long userId = Long.parseLong(name);
            return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

    private StockOutReasonEnum mapToReasonEnum(StockOutType type) {
        if (type == null) return null;
        try {
            return StockOutReasonEnum.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            return null; // Fallback or handle appropriately
        }
    }

    public List<StockOutResponse> createStockOutBatch(CreateStockOutBatchRequest request) {
        String referenceNumber = "OUT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        List<StockOut> stockOuts = new ArrayList<>();
        User currentUser = resolveCurrentUser();

        Warehouse sourceWarehouse = null;
        if (request.getSourceWarehouseId() != null) {
            sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Source warehouse not found"));
        }

        Warehouse branch = null;
        if (request.getStockOutType() == StockOutType.BRANCH_TRANSFER) {
            if (request.getBranchId() == null) throw new IllegalArgumentException("Destination branch is required for transfer");
            branch = warehouseRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Destination branch not found"));
        }

        Employee employee = null;
        if (request.getStockOutType() == StockOutType.EMPLOYEE) {
            if (request.getEmployeeId() == null) throw new IllegalArgumentException("Employee is required");
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
        }

        for (StockOutItemRequest itemRequest : request.getItems()) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemRequest.getItemId()));

            if (item.getCurrentStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for item " + item.getName() + ". Available: " + item.getCurrentStock());
            }

            Long previousStock = item.getCurrentStock();
            Long newStock = previousStock - itemRequest.getQuantity();

            // Deduct stock
            item.setCurrentStock(newStock);
            itemRepository.save(item);

            // Create StockMovement
            StockMovement movement = new StockMovement(
                item,
                currentUser,
                MovementType.OUT,
                (long) itemRequest.getQuantity(),
                previousStock,
                newStock
            );
            movement.setReferenceNumber(referenceNumber);
            movement.setNotes(request.getNote());
            movement.setReasonType(mapToReasonEnum(request.getStockOutType()));
            movement.setReason(request.getStockOutType().name());
            movement.setWarehouse(sourceWarehouse); // The source warehouse
            
            if (branch != null) {
                movement.setRecipient("Branch: " + branch.getName());
            } else if (employee != null) {
                movement.setRecipient("Employee: " + employee.getName());
            } else {
                movement.setRecipient(request.getStockOutType().name());
            }
            
            stockMovementRepository.save(movement);

            StockOut stockOut = StockOut.builder()
                    .stockOutType(request.getStockOutType())
                    .item(item)
                    .quantity(itemRequest.getQuantity())
                    .stockOutDate(request.getDate() != null ? request.getDate() : LocalDateTime.now())
                    .note(request.getNote())
                    .sourceWarehouse(sourceWarehouse)
                    .branch(branch)
                    .employee(employee)
                    .referenceNumber(referenceNumber)
                    .build();

            stockOuts.add(stockOutRepository.save(stockOut));
        }

        return stockOuts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    public StockOutResponse createStockOut(CreateStockOutRequest request) {
        Long itemId = request.getItemId();
        if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        User currentUser = resolveCurrentUser();

        if (item.getCurrentStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getCurrentStock());
        }
        
        Warehouse sourceWarehouse = null;
        if (request.getSourceWarehouseId() != null) {
            sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Source warehouse not found"));
        }
        
        Warehouse branch = null;
        if (request.getStockOutType() == StockOutType.BRANCH_TRANSFER) {
             if (request.getBranchId() != null) {
                 branch = warehouseRepository.findById(request.getBranchId())
                     .orElseThrow(() -> new RuntimeException("Branch not found"));
             }
        }
        
        Employee employee = null;
        if (request.getStockOutType() == StockOutType.EMPLOYEE) {
             if (request.getEmployeeId() != null) {
                 employee = employeeRepository.findById(request.getEmployeeId())
                     .orElseThrow(() -> new RuntimeException("Employee not found"));
             }
        }

        Long previousStock = item.getCurrentStock();
        Long newStock = previousStock - request.getQuantity();

        // Deduct stock
        item.setCurrentStock(newStock);
        itemRepository.save(item);

        // Create StockMovement
        StockMovement movement = new StockMovement(
            item,
            currentUser,
            MovementType.OUT,
            (long) request.getQuantity(),
            previousStock,
            newStock
        );
        movement.setNotes(request.getNote());
        movement.setReasonType(mapToReasonEnum(request.getStockOutType()));
        movement.setReason(request.getStockOutType().name());
        movement.setWarehouse(sourceWarehouse);
        
        if (branch != null) {
            movement.setRecipient("Branch: " + branch.getName());
        } else if (employee != null) {
            movement.setRecipient("Employee: " + employee.getName());
        } else {
            movement.setRecipient(request.getStockOutType().name());
        }
        
        stockMovementRepository.save(movement);

        StockOut stockOut = StockOut.builder()
                .stockOutType(request.getStockOutType())
                .item(item)
                .quantity(request.getQuantity())
                .stockOutDate(request.getDate() != null ? request.getDate() : LocalDateTime.now())
                .note(request.getNote())
                .sourceWarehouse(sourceWarehouse)
                .branch(branch)
                .employee(employee)
                .build();

        var savedStockOut = stockOutRepository.save(stockOut);
        if (savedStockOut == null) throw new RuntimeException("Saved stock out is null");
        
        return mapToResponse(savedStockOut);
    }
    
    // updateStockOut method remains mostly the same but should handle branch/employee updates if needed.
    // For now, I'll assume updates are just quantity/note/date/type. 
    // If type changes to BRANCH_TRANSFER, we might need branchId.
    // I will leave updateStockOut as is for now or update it minimally.
    // The user asked for "add stock out" page update mostly.

    public StockOutResponse updateStockOut(Long id, CreateStockOutRequest request) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        StockOut stockOut = stockOutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock out not found"));

        Item item = stockOut.getItem(); 
        
        if (!item.getItemId().equals(request.getItemId())) {
            // Restore stock to old item
            item.setCurrentStock(item.getCurrentStock() + stockOut.getQuantity());
            itemRepository.save(item);

            // Get new item
            Long itemId = request.getItemId();
            if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            
            // Check availability on new item
            if (item.getCurrentStock() < request.getQuantity()) {
                throw new RuntimeException("Insufficient stock on new item. Available: " + item.getCurrentStock());
            }
            
            // Deduct from new item
            item.setCurrentStock(item.getCurrentStock() - request.getQuantity());
            stockOut.setItem(item);
        } else {
            // Same item, adjust difference
            int diff = request.getQuantity() - stockOut.getQuantity();
            
            if (diff > 0) {
                // Trying to remove more
                if (item.getCurrentStock() < diff) {
                    throw new RuntimeException("Insufficient stock. Available: " + item.getCurrentStock());
                }
                item.setCurrentStock(item.getCurrentStock() - diff);
            } else if (diff < 0) {
                // Trying to remove less (restore some)
                item.setCurrentStock(item.getCurrentStock() + Math.abs(diff));
            }
        }
        
        itemRepository.save(item);

        stockOut.setStockOutType(request.getStockOutType());
        stockOut.setQuantity(request.getQuantity());
        stockOut.setNote(request.getNote());
        if (request.getDate() != null) {
            stockOut.setStockOutDate(request.getDate());
        }
        
        // Handle branch/employee updates
        if (request.getStockOutType() == StockOutType.BRANCH_TRANSFER && request.getBranchId() != null) {
            stockOut.setBranch(warehouseRepository.findById(request.getBranchId()).orElse(null));
        } else if (request.getStockOutType() != StockOutType.BRANCH_TRANSFER) {
            stockOut.setBranch(null);
        }
        
        if (request.getStockOutType() == StockOutType.EMPLOYEE && request.getEmployeeId() != null) {
            stockOut.setEmployee(employeeRepository.findById(request.getEmployeeId()).orElse(null));
        } else if (request.getStockOutType() != StockOutType.EMPLOYEE) {
            stockOut.setEmployee(null);
        }

        StockOut updatedStockOut = stockOutRepository.save(stockOut);
        return mapToResponse(updatedStockOut);
    }

    public void deleteStockOut(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        StockOut stockOut = stockOutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock out not found"));

        // 1. Restore stock to item
        Item item = stockOut.getItem();
        item.setCurrentStock(item.getCurrentStock() + stockOut.getQuantity());
        itemRepository.save(item);

        // 2. Find and delete associated StockMovement
        // We use reference number and item ID to match
        if (stockOut.getReferenceNumber() != null) {
            List<StockMovement> movements = stockMovementRepository.findByReferenceNumber(stockOut.getReferenceNumber());
            movements.stream()
                    .filter(m -> m.getItem().getItemId().equals(item.getItemId()))
                    .findFirst() // If multiple matches (rare), just delete one
                    .ifPresent(stockMovementRepository::delete);
        }

        // 3. Delete the stock out record
        stockOutRepository.delete(stockOut);
    }

    public List<StockOutResponse> getAllStockOuts() {
        return stockOutRepository.findAllByOrderByStockOutDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StockOutResponse getStockOutById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        StockOut stockOut = stockOutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock out not found"));
        return mapToResponse(stockOut);
    }

    private StockOutResponse mapToResponse(StockOut stockOut) {
        return StockOutResponse.builder()
                .id(stockOut.getId())
                .stockOutType(stockOut.getStockOutType())
                .itemId(stockOut.getItem().getItemId())
                .itemName(stockOut.getItem().getName())
                .itemSku(stockOut.getItem().getSku())
                .quantity(stockOut.getQuantity())
                .stockOutDate(stockOut.getStockOutDate())
                .note(stockOut.getNote())
                .sourceWarehouseId(stockOut.getSourceWarehouse() != null ? stockOut.getSourceWarehouse().getWarehouseId() : null)
                .sourceWarehouseName(stockOut.getSourceWarehouse() != null ? stockOut.getSourceWarehouse().getName() : null)
                .branchId(stockOut.getBranch() != null ? stockOut.getBranch().getWarehouseId() : null)
                .branchName(stockOut.getBranch() != null ? stockOut.getBranch().getName() : null)
                .employeeId(stockOut.getEmployee() != null ? stockOut.getEmployee().getEmployeeId() : null)
                .employeeName(stockOut.getEmployee() != null ? stockOut.getEmployee().getName() : null)
                .referenceNumber(stockOut.getReferenceNumber())
                .build();
    }
}
