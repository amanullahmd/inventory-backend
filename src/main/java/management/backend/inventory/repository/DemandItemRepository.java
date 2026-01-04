package management.backend.inventory.repository;

import management.backend.inventory.entity.DemandItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DemandItemRepository extends JpaRepository<DemandItem, Long> {
    List<DemandItem> findByDemand_DemandId(Long demandId);
}
