package management.backend.inventory.service;

import management.backend.inventory.dto.CreateWarehouseRequest;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.WarehouseRepository;
import management.backend.inventory.dto.UpdateWarehouseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import management.backend.inventory.entity.WarehouseStatus;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Transactional
    public Warehouse createWarehouse(CreateWarehouseRequest request) {
        if (request.getWarehouseCode() != null && !request.getWarehouseCode().isBlank()) {
            Optional<Warehouse> existing = warehouseRepository.findByWarehouseCode(request.getWarehouseCode());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Warehouse code already exists");
            }
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCapacityUnits(request.getCapacityUnits());
        warehouse.setStatus(WarehouseStatus.ACTIVE);
        warehouse.setWarehouseCode(request.getWarehouseCode());
        return warehouseRepository.save(warehouse);
    }
    
    @Transactional
    public Warehouse updateWarehouse(Long id, UpdateWarehouseRequest request) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCapacityUnits(request.getCapacityUnits());
        if (request.getWarehouseCode() != null && !request.getWarehouseCode().isBlank()) {
            Optional<Warehouse> existing = warehouseRepository.findByWarehouseCode(request.getWarehouseCode());
            if (existing.isPresent() && !existing.get().getWarehouseId().equals(id)) {
                throw new IllegalArgumentException("Warehouse code already exists");
            }
            warehouse.setWarehouseCode(request.getWarehouseCode());
        } else {
            warehouse.setWarehouseCode(null);
        }
        if (request.getIsActive() != null) {
            warehouse.setIsActive(request.getIsActive());
        }
        return warehouseRepository.save(warehouse);
    }
    
    @Transactional
    public Warehouse setActive(Long id, Boolean active) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        warehouse.setIsActive(active != null ? active : warehouse.getIsActive());
        return warehouseRepository.save(warehouse);
    }
    
    // Delete disabled per business rule
}
