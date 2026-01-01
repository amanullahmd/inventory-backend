package management.backend.inventory.service;

import management.backend.inventory.dto.CreateTransferRequest;
import management.backend.inventory.entity.Batch;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.StockTransfer;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.BatchRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.StockTransferRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockTransferService {
    private final StockTransferRepository stockTransferRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    public StockTransferService(StockTransferRepository stockTransferRepository,
                                ItemRepository itemRepository,
                                WarehouseRepository warehouseRepository,
                                BatchRepository batchRepository,
                                UserRepository userRepository) {
        this.stockTransferRepository = stockTransferRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StockTransfer> getAllTransfers() {
        return stockTransferRepository.findAll();
    }

    @Transactional
    public StockTransfer createTransfer(CreateTransferRequest request, Authentication authentication) {
        Item item = itemRepository.findById(request.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        Warehouse fromW = warehouseRepository.findById(request.getFromWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("From warehouse not found"));
        Warehouse toW = warehouseRepository.findById(request.getToWarehouseId())
            .orElseThrow(() -> new IllegalArgumentException("To warehouse not found"));
        if (fromW.getWarehouseId().equals(toW.getWarehouseId())) {
            throw new IllegalArgumentException("Source and destination warehouses must be different");
        }
        User currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StockTransfer transfer = new StockTransfer();
        transfer.setItem(item);
        transfer.setFromWarehouse(fromW);
        transfer.setToWarehouse(toW);
        transfer.setQuantity(request.getQuantity());
        transfer.setNotes(request.getNotes());
        transfer.setCreatedBy(currentUser);
        transfer.setStatus("DRAFT");
        if (request.getBatchId() != null) {
            Batch batch = batchRepository.findById(request.getBatchId()).orElse(null);
            transfer.setBatch(batch);
        }
        return stockTransferRepository.save(transfer);
    }
}
