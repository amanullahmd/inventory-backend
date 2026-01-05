package management.backend.inventory.service;

import management.backend.inventory.dto.CreateEmployeeRequest;
import management.backend.inventory.dto.EmployeeResponse;
import management.backend.inventory.dto.UpdateEmployeeRequest;
import management.backend.inventory.entity.Employee;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.repository.EmployeeRepository;
import management.backend.inventory.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final WarehouseRepository warehouseRepository;
    public EmployeeService(EmployeeRepository employeeRepository, WarehouseRepository warehouseRepository) {
        this.employeeRepository = employeeRepository;
        this.warehouseRepository = warehouseRepository;
    }
    
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest req) {
        Employee e = new Employee();
        e.setName(req.getName());
        e.setGrade(req.getGrade());
        e.setPosition(req.getPosition());
        e.setMobileNumber(req.getMobileNumber());
        e.setEmail(req.getEmail());
        e.setAddress(req.getAddress());
        e.setServicePeriod(req.getServicePeriod());
        e.setNidNumber(req.getNidNumber());
        if (req.getBranchId() != null) {
            Long branchId = req.getBranchId();
            if (branchId == null) throw new IllegalArgumentException("Branch ID cannot be null");
            Warehouse w = warehouseRepository.findById(branchId).orElseThrow(() -> new IllegalArgumentException("Branch not found"));
            e.setBranch(w);
        }
        String code = req.getEmployeeCode() != null && !req.getEmployeeCode().isBlank() ? req.getEmployeeCode() : generateCode(req.getName());
        e.setEmployeeCode(code);
        var saved = employeeRepository.save(e);
        if (saved == null) throw new RuntimeException("Saved employee is null");
        return toResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public List<EmployeeResponse> list() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }
    
    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Employee e = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return toResponse(e);
    }
    
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest req) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Employee e = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (req.getEmployeeCode() != null && !req.getEmployeeCode().isBlank()) e.setEmployeeCode(req.getEmployeeCode());
        if (req.getName() != null) e.setName(req.getName());
        if (req.getGrade() != null) e.setGrade(req.getGrade());
        if (req.getPosition() != null) e.setPosition(req.getPosition());
        if (req.getBranchId() != null) {
            Long branchId = req.getBranchId();
            if (branchId == null) throw new IllegalArgumentException("Branch ID cannot be null");
            Warehouse w = warehouseRepository.findById(branchId).orElseThrow(() -> new IllegalArgumentException("Branch not found"));
            e.setBranch(w);
        }
        if (req.getMobileNumber() != null) e.setMobileNumber(req.getMobileNumber());
        if (req.getEmail() != null) e.setEmail(req.getEmail());
        if (req.getAddress() != null) e.setAddress(req.getAddress());
        if (req.getServicePeriod() != null) e.setServicePeriod(req.getServicePeriod());
        if (req.getNidNumber() != null) e.setNidNumber(req.getNidNumber());
        var saved = employeeRepository.save(e);
        if (saved == null) throw new RuntimeException("Saved employee is null");
        return toResponse(saved);
    }
    
    @Transactional
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Employee e = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        employeeRepository.delete(e);
    }
    
    private String generateCode(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        base = base.replaceAll("[^A-Za-z0-9]", "");
        if (base.isBlank()) base = "EMP";
        final String codeBase = base;
        final List<Employee> existing = employeeRepository.findByEmployeeCodeStartsWith(codeBase);
        if (existing.stream().noneMatch(emp -> codeBase.equalsIgnoreCase(emp.getEmployeeCode()))) {
            return base;
        }
        int suffix = 1;
        while (true) {
            String candidate = String.format("%s+%02d", base, suffix);
            final String candi = candidate;
            if (existing.stream().noneMatch(emp -> candi.equalsIgnoreCase(emp.getEmployeeCode()))) {
                return candidate;
            }
            suffix++;
        }
    }
    
    private EmployeeResponse toResponse(Employee e) {
        Long bid = e.getBranch() != null ? e.getBranch().getWarehouseId() : null;
        String bname = e.getBranch() != null ? e.getBranch().getName() : null;
        return new EmployeeResponse(
            e.getEmployeeId(),
            e.getEmployeeCode(),
            e.getName(),
            e.getGrade(),
            e.getPosition(),
            bid,
            bname,
            e.getMobileNumber(),
            e.getEmail(),
            e.getAddress(),
            e.getServicePeriod(),
            e.getNidNumber(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }
}
