package management.backend.inventory.service;

import management.backend.inventory.dto.CreateBatchRequest;
import management.backend.inventory.entity.Batch;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.repository.BatchRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BatchService {
    private final BatchRepository batchRepository;
    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;

    public BatchService(BatchRepository batchRepository, ItemRepository itemRepository, SupplierRepository supplierRepository) {
        this.batchRepository = batchRepository;
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    @Transactional
    public Batch createBatch(CreateBatchRequest request) {
        Long itemId = request.getItemId();
        if (itemId == null) {
             throw new IllegalArgumentException("Item ID is required");
        }
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        Batch batch = new Batch();
        batch.setItem(item);
        batch.setBatchNumber(request.getBatchNumber());
        if (request.getSupplierId() != null) {
            Long supplierId = request.getSupplierId();
            if (supplierId == null) throw new IllegalArgumentException("Supplier ID cannot be null");
            Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            batch.setSupplier(supplier);
        }
        if (request.getExpiryDate() != null && !request.getExpiryDate().isBlank()) {
            batch.setExpiryDate(LocalDate.parse(request.getExpiryDate()));
        }
        if (request.getManufacturingDate() != null && !request.getManufacturingDate().isBlank()) {
            batch.setManufacturingDate(LocalDate.parse(request.getManufacturingDate()));
        }
        batch.setQuantityReceived(request.getQuantityReceived());
        batch.setIsActive(true);
        return batchRepository.save(batch);
    }
}
