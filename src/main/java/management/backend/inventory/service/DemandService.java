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
import management.backend.inventory.repository.WarehouseRepository;
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
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    
    public DemandService(DemandRepository demandRepository, ItemRepository itemRepository, WarehouseRepository warehouseRepository, UserRepository userRepository, DemandItemRepository demandItemRepository, EmployeeRepository employeeRepository) {
        this.demandRepository = demandRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.demandItemRepository = demandItemRepository;
        this.employeeRepository = employeeRepository;
    }
    
    @Transactional
    public DemandResponse create(CreateDemandRequest request, Authentication authentication) {
        Item item = null;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            Long firstItemId = request.getItems().get(0).getItemId();
            item = itemRepository.findById(firstItemId).orElseThrow(() -> new IllegalArgumentException("Item not found: " + firstItemId));
        } else if (request.getItemId() != null) {
            item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        } else {
            throw new IllegalArgumentException("At least one item is required");
        }
        Employee emp = null;
        Warehouse warehouse = null;
        if (request.getEmployeeId() != null) {
            emp = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
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
                Item it = itemRepository.findById(line.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
                DemandItem di = new DemandItem(d, it, line.getUnits() != null ? line.getUnits() : 1);
                demandItemRepository.save(di);
            }
            // set primary item for backward compatibility display
            var first = request.getItems().get(0);
            Item it = itemRepository.findById(first.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found: " + first.getItemId()));
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
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
        return toResponse(d);
    }
    
    @Transactional
    public DemandResponse update(Long id, management.backend.inventory.dto.UpdateDemandRequest request) {
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
        if (request.getDemandCode() != null && !request.getDemandCode().isBlank()) {
            d.setDemandCode(request.getDemandCode());
        }
        if (request.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            d.setEmployee(emp);
            d.setDemanderName(emp.getName());
            d.setPosition(emp.getPosition());
            d.setGrade(emp.getGrade());
            d.setWarehouse(emp.getBranch());
        }
        if (request.getItemId() != null) {
            Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found"));
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
        d = demandRepository.save(d);
        if (request.getItems() != null) {
            // replace items
            for (DemandItem existing : demandItemRepository.findByDemand_DemandId(id)) {
                demandItemRepository.delete(existing);
            }
            if (!request.getItems().isEmpty()) {
                for (var line : request.getItems()) {
                    Item it = itemRepository.findById(line.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found: " + line.getItemId()));
                    DemandItem di = new DemandItem(d, it, line.getUnits() != null ? line.getUnits() : 1);
                    demandItemRepository.save(di);
                }
                var first = request.getItems().get(0);
                Item it = itemRepository.findById(first.getItemId()).orElseThrow(() -> new IllegalArgumentException("Item not found: " + first.getItemId()));
                d.setItem(it);
                d = demandRepository.save(d);
            }
        }
        return toResponse(d);
    }
    
    @Transactional
    public void delete(Long id) {
        Demand d = demandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demand not found"));
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
