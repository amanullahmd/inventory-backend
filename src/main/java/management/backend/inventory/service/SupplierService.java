package management.backend.inventory.service;

import management.backend.inventory.dto.CreateSupplierRequest;
import management.backend.inventory.dto.UpdateSupplierRequest;
import management.backend.inventory.entity.Supplier;
import management.backend.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import management.backend.inventory.entity.SupplierStatus;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional
    public Supplier createSupplier(CreateSupplierRequest request) {
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().isBlank()) {
            Optional<Supplier> existing = supplierRepository.findByRegistrationNumber(request.getRegistrationNumber());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Registration number already exists");
            }
        }
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setRegistrationNumber(request.getRegistrationNumber());
        supplier.setStatus(SupplierStatus.ACTIVE);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setContactPerson(request.getContactPerson());
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().isBlank()) {
            Optional<Supplier> existing = supplierRepository.findByRegistrationNumber(request.getRegistrationNumber());
            if (existing.isPresent() && !existing.get().getSupplierId().equals(id)) {
                throw new IllegalArgumentException("Registration number already exists");
            }
            supplier.setRegistrationNumber(request.getRegistrationNumber());
        } else {
            supplier.setRegistrationNumber(null);
        }
        if (request.getIsActive() != null) {
            supplier.setIsActive(request.getIsActive());
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier setActive(Long id, Boolean active) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        supplier.setIsActive(active != null ? active : supplier.getIsActive());
        return supplierRepository.save(supplier);
    }

    // Delete operation removed per business rules
}
