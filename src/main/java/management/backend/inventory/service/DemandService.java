package management.backend.inventory.service;

import management.backend.inventory.dto.CreateDemandRequest;
import management.backend.inventory.dto.DemandResponse;
import management.backend.inventory.entity.Demand;
import management.backend.inventory.entity.DemandItem;
import management.backend.inventory.entity.Item;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Employee;
import management.backend.inventory.entity.Warehouse;
import management.backend.inventory.entity.DemandStatus;
import management.backend.inventory.repository.DemandRepository;
import management.backend.inventory.repository.ItemRepository;
import management.backend.inventory.repository.DemandItemRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DemandService {
    private final DemandRepository demandRepository;
    private final ItemRepository itemRepository;
    private final DemandItemRepository demandItemRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    
    public DemandService(DemandRepository demandRepository, ItemRepository itemRepository, UserRepository userRepository, DemandItemRepository demandItemRepository, EmployeeRepository employeeRepository) {
        this.demandRepository = demandRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.demandItemRepository = demandItemRepository;
        this.employeeRepository = employeeRepository;
    }
    
    @Transactional
    public DemandResponse create(CreateDemandRequest request, Authentication authentication) {
        Item item = null;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            Long firstItemId = request.getItems().get(0).getItemId();
            if (firstItemId == null) throw new IllegalArgumentException("Item ID in line items cannot be null");
            item = itemRepository.findById(firstItemId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + firstItemId));
        } else if (request.getItemId() != null) {
            Long itemId = request.getItemId();
            if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
            item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        } else {
            throw new IllegalArgumentException("At least one item is required");
        }
        Employee emp = null;
        Warehouse warehouse = null;
        if (request.getEmployeeId() != null) {
            Long empId = request.getEmployeeId();
            if (empId == null) throw new IllegalArgumentException("Employee ID cannot be null");
            emp = employeeRepository.findById(empId).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            warehouse = emp.getBranch();
        }
        User user = resolveCurrentUser(authentication);
        Demand d = new Demand();
        if (emp != null) {
            d.setEmployee(emp);
            d.setDemanderName(emp.getName());
            d.setPosition(emp.getPosition());
            d.setGrade(emp.getGrade());
        }
        d.setItem(item);
        d.setUnit(request.getUnit());
        d.setWarehouse(warehouse);
        d.setRequestedBy(user);
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                DemandStatus st = DemandStatus.valueOf(request.getStatus().toUpperCase());
                d.setStatus(st);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.getStatus());
            }
        }
        d.setNote(request.getNote());
        d = demandRepository.save(d);
        String code = "DM-" + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + d.getDemandId();
        d.setDemandCode(code);
        d = demandRepository.save(d);
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (var line : request.getItems()) {
                Long itemId = line.getItemId();
                if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
                Item it = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
                DemandItem di = new DemandItem(d, it, line.getUnits() != null ? line.getUnits() : 1);
                demandItemRepository.save(di);
            }
            // set primary item for backward compatibility display
            var first = request.getItems().get(0);
            Long firstId = first.getItemId();
            if (firstId == null) throw new IllegalArgumentException("Item ID cannot be null");
            Item it = itemRepository.findById(firstId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + firstId));
            d.setItem(it);
            d = demandRepository.save(d);
        } else {
            DemandItem di = new DemandItem(d, item, 1);
            demandItemRepository.save(di);
        }
        return toResponse(d);
    }
    
    @Transactional(readOnly = true)
    public List<DemandResponse> list() {
        return demandRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }
    
    @Transactional(readOnly = true)
    public DemandResponse get(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
        return toResponse(d);
    }
    
    @Transactional
    public DemandResponse update(Long id, management.backend.inventory.dto.UpdateDemandRequest request) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
        if (request.getDemandCode() != null && !request.getDemandCode().isBlank()) {
            d.setDemandCode(request.getDemandCode());
        }
        if (request.getEmployeeId() != null) {
            Long empId = request.getEmployeeId();
            if (empId == null) throw new IllegalArgumentException("Employee ID cannot be null");
            Employee emp = employeeRepository.findById(empId).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            d.setEmployee(emp);
            d.setDemanderName(emp.getName());
            d.setPosition(emp.getPosition());
            d.setGrade(emp.getGrade());
            d.setWarehouse(emp.getBranch());
        }
        if (request.getItemId() != null) {
            Long itemId = request.getItemId();
            if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
            Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
            d.setItem(item);
        }
        if (request.getUnit() != null) d.setUnit(request.getUnit());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                DemandStatus st = DemandStatus.valueOf(request.getStatus().toUpperCase());
                d.setStatus(st);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.getStatus());
            }
        }
        if (request.getNote() != null) d.setNote(request.getNote());
        var saved = demandRepository.save(d);
        if (saved == null) throw new RuntimeException("Saved demand is null");
        if (request.getItems() != null) {
            // replace items
            for (DemandItem existing : demandItemRepository.findByDemand_DemandId(id)) {
                if (existing == null) continue;
                demandItemRepository.delete(existing);
            }
            if (!request.getItems().isEmpty()) {
                for (var line : request.getItems()) {
                    Long itemId = line.getItemId();
                    if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
                    Item it = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
                    DemandItem di = new DemandItem(saved, it, line.getUnits() != null ? line.getUnits() : 1);
                    demandItemRepository.save(di);
                }
                var first = request.getItems().get(0);
                Long firstId = first.getItemId();
                if (firstId == null) throw new IllegalArgumentException("Item ID cannot be null");
                Item it = itemRepository.findById(firstId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + firstId));
                saved.setItem(it);
                saved = demandRepository.save(saved);
            }
        }
        return toResponse(saved);
    }
    
    @Transactional
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
        if (d == null) throw new IllegalArgumentException("Demand not found");
        demandRepository.delete(d);
    }
    
    private DemandResponse toResponse(Demand d) {
        Long wid = d.getWarehouse() != null ? d.getWarehouse().getWarehouseId() : null;
        String wname = d.getWarehouse() != null ? d.getWarehouse().getName() : null;
        java.util.List<management.backend.inventory.dto.DemandItemResponse> itemDtos =
            demandItemRepository.findByDemand_DemandId(d.getDemandId()).stream()
                .map(di -> new management.backend.inventory.dto.DemandItemResponse(
                    di.getDemandItemId(),
                    di.getItem().getItemId(),
                    di.getItem().getSku(),
                    di.getItem().getName(),
                    di.getUnits()
                ))
                .toList();
        if (itemDtos.isEmpty() && d.getItem() != null) {
            itemDtos = java.util.List.of(new management.backend.inventory.dto.DemandItemResponse(
                null,
                d.getItem().getItemId(),
                d.getItem().getSku(),
                d.getItem().getName(),
                1
            ));
        }
        Long eid = d.getEmployee() != null ? d.getEmployee().getEmployeeId() : null;
        String ecode = d.getEmployee() != null ? d.getEmployee().getEmployeeCode() : null;
        return new DemandResponse(
            d.getDemandId(),
            d.getDemandCode(),
            eid,
            ecode,
            d.getDemanderName(),
            d.getPosition(),
            d.getGrade(),
            d.getStatus() != null ? d.getStatus().name() : null,
            d.getItem().getItemId(),
            d.getItem().getName(),
            d.getItem().getSku(),
            d.getUnit(),
            wid,
            wname,
            d.getRequestedBy() != null ? d.getRequestedBy().getName() : null,
            d.getCreatedAt(),
            d.getUpdatedAt(),
            d.getNote(),
            itemDtos
        );
    }
    
    private User resolveCurrentUser(Authentication authentication) {
        String sub = authentication.getName();
        try {
            Long id = Long.parseLong(sub);
            return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(sub).orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
    }
}
